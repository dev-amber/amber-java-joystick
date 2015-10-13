package pl.edu.agh.amber;

import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;
import pl.edu.agh.amber.common.AmberClient;
import pl.edu.agh.amber.roboclaw.RoboclawProxy;

import java.io.IOException;

public class AmberJoystick implements JoystickListener, Runnable {

    private final int max_speed;
    private final AmberClient client;
    private final RoboclawProxy roboclawProxy;

    private boolean stop = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        new AmberJoystick(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    public AmberJoystick(String address, int joy, int max_speed) throws IOException {
        this.max_speed = max_speed;
        client = new AmberClient(address, 26233);
        roboclawProxy = new RoboclawProxy(client, 0);
        Runtime.getRuntime().addShutdownHook(new Thread(this));

        Joystick joystick = Joystick.createInstance(joy);
        joystick.addJoystickListener(this);
    }

    @Override
    public synchronized void joystickAxisChanged(Joystick joystick) {
        float x = joystick.getX();
        float y = joystick.getY();

        float lSpeed;
        float rSpeed;
        float lAsym;
        float rAsym;

        int motorsLeftSpeed;
        int motorsRightSpeed;

        if (x < 0) {
            lAsym = -Math.abs(x);
            rAsym = Math.abs(x);
        } else {
            lAsym = Math.abs(x);
            rAsym = -Math.abs(x);
        }

        lSpeed = -y;
        rSpeed = -y;

        lSpeed += lAsym;
        rSpeed += rAsym;

        if (Math.abs(lSpeed) > 1.0) {
            lSpeed = Math.signum(lSpeed);
        }

        if (Math.abs(rSpeed) > 1.0) {
            rSpeed = Math.signum(rSpeed);
        }


        if (stop || (Math.abs(rSpeed) <= 0.001 && Math.abs(lSpeed) <= 0.001)) {
            motorsLeftSpeed = 1;
            motorsRightSpeed = 1;
        } else {
            motorsRightSpeed = (int) (rSpeed * max_speed);
            motorsLeftSpeed = (int) (lSpeed * max_speed);
        }

        System.err.println(motorsLeftSpeed + ", " + motorsRightSpeed);
        try {
            roboclawProxy.sendMotorsCommand(motorsLeftSpeed, motorsRightSpeed, motorsLeftSpeed, motorsRightSpeed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joystickButtonChanged(Joystick arg0) {
        if (arg0.isButtonDown(1)) {
            stop = !stop;
        }
    }

    @Override
    public void run() {
        client.terminate();
    }
}
