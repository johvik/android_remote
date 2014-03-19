package android.remote;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public ConnectionThread(final PublicKey publicKey, final String dstName, final int dstPort,
                            final String user, final String password) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(dstName, dstPort);
                    mInput = mSocket.getInputStream();
                    mOutput = mSocket.getOutputStream();
                    mClientProtocol = new ClientProtocol(publicKey, mInput, mOutput);
                    mClientProtocol.authenticate(user, password);
                    // Receive authentication response packet
                    Packet packet = mClientProtocol.nextPacket();
                    mClientProtocol.process(packet);
                    ConnectionThread.super.start(); // Start running
                    return;
                } catch (PacketException e) {
                    // disconnect
                } catch (IOException e) {
                    // disconnect
                } catch (ProtocolException e) {
                    // disconnect
                } catch (GeneralSecurityException e) {
                    // disconnect
                } catch (NullPointerException e) {
                    // disconnect
                }
                disconnect();
            }
        });
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
        } catch (PacketException e) {
            // terminate
        } catch (IOException e) {
            // terminate
        } catch (ProtocolException e) {
            // terminate
        } catch (NullPointerException e) {
            // terminate
        }
        disconnect();
        mThreadPool.shutdown();
        Log.d("Run", "No longer running...");
    }

    public void disconnect() {
        Log.d("Disconnect", "=(");
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
    }

    public void commandRequest(final Command command) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mClientProtocol.commandRequest(command);
                    return;
                } catch (PacketException e) {
                    // disconnect
                } catch (IOException e) {
                    // disconnect
                } catch (ProtocolException e) {
                    // disconnect
                } catch (NullPointerException e) {
                    // disconnect
                }
                disconnect();
            }
        });
    }

    public void terminateRequest(final boolean shutdown) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mClientProtocol.terminateRequest(shutdown);
                    return;
                } catch (PacketException e) {
                    // disconnect
                } catch (IOException e) {
                    // disconnect
                } catch (ProtocolException e) {
                    // disconnect
                } catch (NullPointerException e) {
                    // disconnect
                }
                disconnect();
            }
        });
    }
}
