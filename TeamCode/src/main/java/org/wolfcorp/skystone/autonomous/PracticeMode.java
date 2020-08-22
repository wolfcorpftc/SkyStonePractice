package org.wolfcorp.skystone.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Practice Mode", group = "Auto")
public class PracticeMode extends SkystoneAuto {
    @Override
    public void runOpMode() throws InterruptedException {
        prologue();
        for (int i = 0; i < 4; i++) {
            forward(1, 5);
            sleep(100);
            turnLeft(0.5,13);
            sleep(100);
        }
    }
}
