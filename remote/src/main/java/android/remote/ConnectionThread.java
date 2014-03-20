package android.remote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import remote.api.ClientProtocol;
import remote.api.Packet;
import remote.api.commands.Command;
import remote.api.exceptions.PacketException;
import remote.api.exceptions.ProtocolException;

public class ConnectionThread extends Thread {
    private final ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(1, 1, 1,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private Socket mSocket = null;
    private InputStream mInput = null;
    private OutputStream mOutput = null;
    private ClientProtocol mClientProtocol = null;
    private ConnectionState mConnectionState = ConnectionState.PENDING;
    private ConnectionCallback mConnectionCallback = null;

    public ConnectionThread(final PublicKey publicKey, final String dstName, final int dstPort,
                            final String user, final String password,
                            ConnectionCallback connectionCallback) {
        mConnectionCallback = connectionCallback;
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket();
                    mSocket.connect(new InetSocketAddress(dstName, dstPort), 10000); // 10 s
                    mInput = new BufferedInputStream(mSocket.getInputStream());
                    mOutput = new BufferedOutputStream(mSocket.getOutputStream());
                    mClientProtocol = new ClientProtocol(publicKey, mInput, mOutput);
                    mClientProtocol.authenticate(user, password);
                    // Receive authentication response packet
                    Packet packet = mClientProtocol.nextPacket();
                    mClientProtocol.process(packet);
                    mConnectionState = ConnectionState.CONNECTED;
                    ConnectionThread.super.start(); // Start running
                    if (mConnectionCallback != null) {
                        mConnectionCallback.onConnect();
                    }
                } catch (PacketException e) {
                    disconnect(e);
                } catch (IOException e) {
                    disconnect(e);
                } catch (ProtocolException e) {
                    disconnect(e);
                } catch (GeneralSecurityException e) {
                    disconnect(e);
                } catch (NullPointerException e) {
                    disconnect(e);
                }
            }
        });
    }

    public synchronized void setConnectionCallback(ConnectionCallback connectionCallback) {
        mConnectionCallback = connectionCallback;
    }

    public synchronized boolean isConnected() {
        return mConnectionState == ConnectionState.CONNECTED;
    }

    @Override
    public synchronized void start() {
        throw new UnsupportedOperationException("ConnectionThread will start by itself");
    }

    @Override
    public void run() {
        try {
            // Read packets until an exception is thrown
            Packet packet;
            while ((packet = mClientProtocol.nextPacket()) != null) {
                mClientProtocol.process(packet);
            }
            disconnect(new EOFException("No more packets"));
        } catch (PacketException e) {
            disconnect(e);
        } catch (IOException e) {
            disconnect(e);
        } catch (ProtocolException e) {
            disconnect(e);
        } catch (NullPointerException e) {
            disconnect(e);
        }
        mThreadPool.shutdown();
    }

    public synchronized void disconnect(Exception e) {
        if (mConnectionState != ConnectionState.CLOSED) {
            mConnectionState = ConnectionState.CLOSED;
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    // Synchronize to avoid null pointers
                    synchronized (mThreadPool) {
                        if (mSocket != null) {
                            try {
                                mSocket.close();
                            } catch (IOException e) {
                                // ignore
                            }
                            mSocket = null;
                        }
                        if (mInput != null) {
                            try {
                                mInput.close();
                            } catch (IOException e) {
                                // ignore
                            }
                            mInput = null;
                        }
                        if (mOutput != null) {
                            try {
                                mOutput.close();
                            } catch (IOException e) {
                                // ignore
                            }
                            mOutput = null;
                        }
                        if (mClientProtocol != null) {
                            mClientProtocol = null;
                        }
                    }
                }
            });
            if (mConnectionCallback != null) {
                mConnectionCallback.onDisconnect(e);
            }
        }
    }

    public synchronized void commandRequest(final Command command) {
        if (mConnectionState == ConnectionState.CONNECTED) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mClientProtocol.commandRequest(command);
                    } catch (PacketException e) {
                        disconnect(e);
                    } catch (IOException e) {
                        disconnect(e);
                    } catch (ProtocolException e) {
                        disconnect(e);
                    } catch (NullPointerException e) {
                        disconnect(e);
                    }
                }
            });
        }
    }

    public synchronized void terminateRequest(final boolean shutdown) {
        if (mConnectionState == ConnectionState.CONNECTED) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mClientProtocol.terminateRequest(shutdown);
                    } catch (PacketException e) {
                        disconnect(e);
                    } catch (IOException e) {
                        disconnect(e);
                    } catch (ProtocolException e) {
                        disconnect(e);
                    } catch (NullPointerException e) {
                        disconnect(e);
                    }
                }
            });
        }
    }

    private enum ConnectionState {
        PENDING, CONNECTED, CLOSED
    }

    public interface ConnectionCallback {
        public void onConnect();

        public void onDisconnect(Exception e);
    }
}
