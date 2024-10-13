package app;

import java.util.ArrayList;
import java.util.Calendar;

public class Histories {
    protected ArrayList<UserHistory> histories = new ArrayList<>();

    public void updateReturnDay(String docName) {
        for (UserHistory detail : histories) {
            if ((detail.returnDay == null) && (detail.docName.equals(docName))) {
                detail.returnDay = Calendar.getInstance();
                return;
            }
        }
    }

    public void add(UserHistory newHistory) {
        histories.add(newHistory);
    }

    public String toString() {
        if (histories.isEmpty()) {
            return "Do not have history!";
        }

        String ans = new String();
        for (UserHistory detail : histories) {
            ans += detail.toString();
        }

        return ans;
    }
}
