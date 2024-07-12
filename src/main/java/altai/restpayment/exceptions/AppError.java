package altai.restpayment.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class AppError {
    private int status;
    private String message;
    private Date timestamp;

    public AppError(int status, String message) {
        this.message = message;
        this.status = status;
        this.timestamp = new Date();
    }
}
