package quinzical.util;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Connect {
    private static Connect instance;

    private Connect() { }

    public static Connect getInstance() {
        if (instance == null) {
            instance = new Connect();
        }
        return instance;
    }

    private Socket socket;

    public void forceConnect() {
        if (socket != null) {
            socket.disconnect();
        }
        makeConnection();
    }
    public Socket getSocket() {
        if (socket == null) {
            makeConnection();
        }
        return socket;
    }

    private void makeConnection() {
        try {
            socket = IO.socket("http://localhost:3000/?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZjhlNjg1Mzg4NWE4M2Y1MDQxNDA4NjEiLCJ1c2VybmFtZSI6ImhlbGxvQGdtYWlsLmNvbSIsImlhdCI6MTYwMzE3NjU5Nn0.uQIGwTMX8smWEKWEdFPZTjR_5sbvYNLhAHSSCCJiIY0");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
				}
                
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
				}

            });
            socket.connect();
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}