package service;

import java.time.LocalDateTime;

public class SystemDateTimeService implements DateTimeService {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}