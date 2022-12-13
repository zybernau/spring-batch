package com.spratch.spratch;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MadWait {

    public void waiter() {
        for (int mi = 0; mi < 5; mi++) {
            for (long i = 0; i < 999999999; i++) {
                Double l = 0.1;
                l = i + i * l * 0.0002;
                double m = Math.log(l);
                double n = Math.log(m);
                madness(l, m, n);
            }
        }
    }
    private void madness(double l, double m, double n) {
        for (int i = 0; i < 10; i++) {
            l += m * n + m / n + m * n + m/n +m * n + m/n +m * n + m/n + m/n +m * n + m/n+ m/n +m * n;
        }
    }
}
