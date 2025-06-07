package com.example.myapplication.controller;

import android.util.Log;

import com.example.myapplication.data.database.TrafficSignDBHelper;
import com.example.myapplication.data.model.TrafficSign;
import com.example.myapplication.view.LearnActivity;

import java.util.ArrayList;
import java.util.List;

public class LearnController {
    private TrafficSignDBHelper dbHelper;
    private List<TrafficSign> signList;
    public boolean isHaveStudying;
    private int sizeIsNotStarted;
    private int sizeIsStudying;
    private int sizeIsLeaned;
    private int totalSize;
    private List<TrafficSign> signListCur;
    private List<TrafficSign> signListIsLeaned;
    private List<TrafficSign> signListIsStudying;
    private List<TrafficSign> signListIsNotStarted;
    private TrafficSign signInProcess;

    public LearnController(LearnActivity context) {
        this.dbHelper = new TrafficSignDBHelper(context);
        this.signList = new ArrayList<>();
        signListCur = new ArrayList<>();
        signListIsLeaned = new ArrayList<>();
        signListIsStudying = new ArrayList<>();
        signListIsNotStarted = new ArrayList<>();
        signInProcess = null;
        sizeIsNotStarted = -1;
        sizeIsStudying = -1;
        sizeIsLeaned = -1;
        totalSize = -1;
        isHaveStudying = false;
    }

    public void setSignList(List<TrafficSign> signList) {
        this.signList = signList;
    }

    private void initLearn() {
        for (TrafficSign sign : signList) {
            switch (sign.getStatus()) {
                case "learned":
                    signListIsLeaned.add(sign);
                    break;
                case "studying":
                    signListIsStudying.add(sign);
                    Log.e ("IP_SIGN: ", sign.getId() + "");
                    break;
                case "not_started":
                    signListIsNotStarted.add(sign);
                    break;
                case "in_progress":
                    signInProcess = sign;
                    Log.e("in_progress", "have");
                    break;
            }
        }

        totalSize = signList.size();
        sizeIsLeaned = signListIsLeaned.size();
        sizeIsStudying = signListIsStudying.size();
        sizeIsNotStarted = signListIsNotStarted.size();
        Log.e("sizeIsStudying", sizeIsStudying + "");
        Log.e("sizeIsLeaned", sizeIsLeaned + "");
        Log.e("totalSize", totalSize + "");
    }

    public List<TrafficSign> getTrafficSigns(String type) {
        return dbHelper.getTrafficSigns(type);
    }

    private boolean haveNotStarted() {
        for (TrafficSign sign : signList) {
            if ("not_started".equals(sign.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private void refreshSignListCur() {
        signList.clear();
        signList = new ArrayList<>(signListCur);
        signListIsLeaned.clear();
        signListIsStudying.clear();
        signListIsNotStarted.clear();
        signInProcess = null;
        initLearn();
    }
    public List<TrafficSign> getSignListCur(boolean isReset) {
        initLearn();

        // Trường hợp 1: Tất cả đều chưa học -> bắt đầu học từ phần tử đầu tiên
        if (sizeIsNotStarted == totalSize) {
            Log.e("CASE_TS", "1");

            signList.get(0).setStatus("in_progress");
            updateTrafficSignStatus(signList.get(0).getId(), "in_progress");
            signListCur = new ArrayList<>(signList);
        }

        // Trường hợp 2: Còn một số biển chưa học -> tiếp tục học bình thường
        else if (sizeIsNotStarted > 0) {
            Log.e("CASE_TS", "2");
            signListCur = new ArrayList<>(signList);
        }

        // Trường hợp 3: Không còn biển nào chưa học
        else if (signInProcess == null) {
            if (sizeIsStudying == totalSize) {
                Log.e("CASE_TS", "3");
                // Tất cả đang ở trạng thái studying (chưa chọn biển đang học) -> bắt đầu học từ biển đầu
                isHaveStudying = true;
                signListIsStudying.get(0).setStatus("in_progress");
                updateTrafficSignStatus(signListIsStudying.get(0).getId(), "in_progress");
                signListCur = new ArrayList<>(signListIsStudying);

                refreshSignListCur();
            } else if (sizeIsLeaned == totalSize || isReset) {
                Log.e("", "4");
                // Đã học hết -> reset lại để học lại từ đầu
                signListCur = restartSignList();
                initLearn();
            } else {
                Log.e("CASE_TS", "5");
                // Vẫn còn danh sách đang học -> chọn tiếp biển đầu
                isHaveStudying = true;
                signListIsStudying.get(0).setStatus("in_progress");
                updateTrafficSignStatus(signListIsStudying.get(0).getId(), "in_progress");
                signListCur = new ArrayList<>(signListIsLeaned);
                signListCur.addAll(signListIsStudying);

                refreshSignListCur();
            }
        }

        // Trường hợp 4: Có biển đang học (in_progress)
        else {
            Log.e("TYPE", "4");
            isHaveStudying = true;
            signListCur = new ArrayList<>(signListIsLeaned);
            signListCur.add(signInProcess);
            signListCur.addAll(signListIsStudying);

            refreshSignListCur();
            Log.e("signListCur", signListCur.size() + "");
        }

        return signListCur;
    }

    public int curIndexInProcess() {
        for (int i = 0; i < signList.size(); i++) {
            if ("in_progress".equals(signList.get(i).getStatus())) {
                return i;
            }
        }
        return -1;
    }

    public void updateTrafficSignStatus(String id, String status) {
        dbHelper.updateTrafficSignStatus(id, status);
    }

    public List<TrafficSign> restartSignList() {
        signListIsLeaned.clear();
        signListIsStudying.clear();
        signListIsNotStarted.clear();
        signInProcess = null;

        for (TrafficSign sign : signList) {
            sign.setStatus("not_started");
            updateTrafficSignStatus(sign.getId(), "not_started");
        }

        signList.get(0).setStatus("in_progress");
        updateTrafficSignStatus(signList.get(0).getId(), "in_progress");

        return signList;
    }


    public void nextSign(int curIndex) {
        updateTrafficSignStatus(signListCur.get(curIndex).getId(), "learned");
        signListCur.get(curIndex).setStatus("learned");

        updateTrafficSignStatus(signListCur.get(curIndex + 1).getId(), "in_progress");
        signListCur.get(curIndex + 1).setStatus("in_progress");
    }

    public void noSign(int curIndex) {
        updateTrafficSignStatus(signListCur.get(curIndex).getId(), "studying");
        signListCur.get(curIndex).setStatus("studying");

        updateTrafficSignStatus(signListCur.get(curIndex + 1).getId(), "in_progress");
        signListCur.get(curIndex + 1).setStatus("in_progress");
    }

    public boolean backSign(int curIndex, boolean type) {
        if (signListCur.get(curIndex) == null) Log.e("ERRO", "Null");
        boolean isStuding = "studying".equals(signListCur.get(curIndex - 1).getStatus());
        if (!type) {
            Log.e("type2",  "true");
            updateTrafficSignStatus(signListCur.get(curIndex).getId(), "not_started");
            Log.e("type2", dbHelper.getTrafficSignById(signListCur.get(curIndex).getId()).getStatus() + "");
            signListCur.get(curIndex).setStatus("not_started");

            updateTrafficSignStatus(signListCur.get(curIndex - 1).getId(), "in_progress");
            signListCur.get(curIndex - 1).setStatus("in_progress");
        } else {
            updateTrafficSignStatus(signListCur.get(curIndex).getId(), "studying");
            signListCur.get(curIndex).setStatus("studying");

            updateTrafficSignStatus(signListCur.get(curIndex - 1).getId(), "in_progress");
            signListCur.get(curIndex - 1).setStatus("in_progress");
        }
        return isStuding;
    }

    public void endSign(int curIndex, boolean type) {
        if (type) {
            updateTrafficSignStatus(signListCur.get(curIndex).getId(), "learned");
            signListCur.get(curIndex).setStatus("learned");
        } else {
            updateTrafficSignStatus(signListCur.get(curIndex).getId(), "studying");
            signListCur.get(curIndex).setStatus("studying");
        }
    }

    public int sizeSignListIsLeaned() {
        return signListIsLeaned.size();
    }

    public int sizeSignListIsStudying() {
        return signListIsStudying.size();
    }

    public int sizeSignListIsNotStarted() {
        return signListIsNotStarted.size();
    }
}
