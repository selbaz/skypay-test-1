package org.example;

import java.time.LocalDate;

public class RealDateProvider implements DateProvider {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}