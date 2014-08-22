package pl.edu.agh.amber;

import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;
import pl.edu.agh.amber.common.AmberClient;
import pl.edu.agh.amber.roboclaw.RoboclawProxy;

import java.io.IOException;

public class AmberJoystick implements JoystickListener, Runnable {

    private float x;
    private float y;
    private boolean stop = true;

    public static void main(String[] args) {
        (new AmberJoystick()).runDemo(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    public void runDemo(String address, int joy, int max_speed) {

        try {
            Joystick joystick = Joystick.createInstance(joy);
            joystick.addJoystickListener(this);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        AmberClient client;
        try {
            client = new AmberClient(address, 26233);

            RoboclawProxy roboclawProxy = new RoboclawProxy(client, 0);

            float lSpeed;
            float rSpeed;
            float lAsym;
            float rAsym;

            int motorsLeftSpeed;
            int motorsRightSpeed;

            while (true) {

                synchronized (this) {

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


                    if (stop || (Math.abs(rSpeed) <= 0.2 && Math.abs(lSpeed) <= 0.2)) {
                        motorsLeftSpeed = 1;
                        motorsRightSpeed = 1;
                    } else {
                        motorsRightSpeed = (int) (rSpeed * max_speed);
                        motorsLeftSpeed = (int) (lSpeed * max_speed);
                    }
                }

                roboclawProxy.sendMotorsCommand(motorsLeftSpeed, motorsRightSpeed, motorsLeftSpeed, motorsRightSpeed);
                Thread.sleep(10);
            }


        } catch (IOException e) {
            System.err.println("Connection error: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void joystickAxisChanged(Joystick joystick) {
        x = joystick.getX();
        y = joystick.getY();

    }

    @Override
    public void joystickButtonChanged(Joystick arg0) {
        if (arg0.isButtonDown(0)) {
            stop = !stop;
        }
    }

    @Override
    public void run() {
    }
}
