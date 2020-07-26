package org.wolfcorp.skystone;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Incrementor", group="Misc")
public class Incrementor extends SkystoneOpMode {

    public static double delay = 0;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        double increment = 1;
        runtime.reset();
        while (opModeIsActive()) {

            if (gamepad1.right_bumper && delay < 30) {
                delay += increment;

                if (delay > 30) {
                    delay = 30;
                }
                telemetry.addData("", "delay: "
                        + Double.toString(delay)
                        + "\nincrement: "
                        + Double.toString(increment));
                telemetry.update();
                sleep(200);
            }

            if (gamepad1.left_bumper && delay > 0) {
                delay -= increment;

                if (delay < 0) {
                    delay = 0;
                }
                telemetry.addData("", "delay: "
                        + Double.toString(delay)
                        + "\nincrement: "
                        + Double.toString(increment));
                telemetry.update();
                sleep(200);
            }

            if (gamepad1.dpad_down) {
                stop();
            }
        }
    }
}