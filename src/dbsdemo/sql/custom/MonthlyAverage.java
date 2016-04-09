package dbsdemo.sql.custom;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Baxos
 */
public class MonthlyAverage {
    
    private final String monthName;
    private double monthAvg;
    
    public MonthlyAverage(String monthName){
        this.monthName = monthName;
        this.monthAvg = 0.0;
    }
    
    public MonthlyAverage(int monthNum){
        this.monthName = monthNumberToString(monthNum);
        this.monthAvg = 0.0;
    }
    
    private String monthNumberToString(int number){
        
        String month;
        
        switch(number){
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
            default:
                month = "Unknown";
                break;
        }
        
        return month;
    }

    public String getMonthName() {
        return monthName;
    }

    public double getMonthAvg() {
        return monthAvg;
    }

    public void setMonthAvg(double monthAvg) {
        this.monthAvg = monthAvg;
    }
}
