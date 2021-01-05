package com.csp.kakaochatbot;

import android.app.Notification;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PushNotificationListenerService extends NotificationListenerService {
    //해당 앱을 실행후 카카오계정을 방에 초대한다음 명령어 입력시 메시지를 답장해주는 봇


    private static final String KAKAO_PACKAGE = "com.kakao.talk";
    private static final char SUFFIX = '/';
    private static final String COMMON = "COMMON";
    private static final String ROOM = "ROOM";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("NotificationListener", "[snowdeer] onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("NotificationListener", "[snowdeer] onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("NotificationListener", "[snowdeer] onDestroy()");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String packageName = sbn.getPackageName();

        if (packageName.equals(KAKAO_PACKAGE)) {

            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;

            String postTime = new Date(sbn.getPostTime()).toString();
            String sender = extras.getString(Notification.EXTRA_TITLE);
            CharSequence message = extras.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence roomName = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

            Log.i("NotificationListener", "[snowdeer] onNotificationPosted() - " + sbn.toString());
            Log.i("NotificationListener", "[snowdeer] PackageName:" + sbn.getPackageName());
            Log.i("NotificationListener", "[snowdeer] PostTime:" + postTime);
            Log.i("NotificationListener", "[snowdeer] sender:" + sender);
            Log.i("NotificationListener", "[snowdeer] message:" + message);
            Log.i("NotificationListener", "[snowdeer] roomName:" + roomName);

            if (sender != null && message != null) {

                String originMessage = message.toString();

                String[] splitedString = originMessage.split(" ");
                //  /info

                String trimSplit = splitedString[0].trim();

                boolean valied = trimSplit.charAt(0) == SUFFIX;
                String command = trimSplit.substring(1);

                List<String> parameter = new ArrayList<>();

                for (int i = 1; i < splitedString.length; i++) {
                    String param = splitedString[i].trim();
                    parameter.add(param);
                }

                Log.d("test", String.valueOf(valied) + command + parameter.toString());

                if (valied && notification.actions != null && !command.equals("") && isvaliedCommend(command)) {

                    COMMAND cmd = COMMAND.valueOf(command);

                    if (cmd.getValue()[0].equals(COMMON)) {
                        processCommon(notification, cmd, parameter);
                    } else {
                        processRoom(notification, cmd, roomName, parameter);
                    }


                }

            }


        }
    }

    private boolean isvaliedCommend(String commend) {

        boolean result = false;

        for (COMMAND item : COMMAND.values()) {

//            Log.d("item", item.getKey() + " " + item.getValue().toString());

            if (commend.equals(item.getKey())) {
                result = true;
                break;
            }

        }

        return result;

    }


    private void processCommon(Notification notification, COMMAND command, List<String> parameter) {
        String replyTxt = "";

        switch (command) {

            case 명령어:

                StringBuilder stringBuilder = new StringBuilder();

                for (COMMAND item : COMMAND.values()) {

                    Log.d("item", item.getKey() + " " + item.getValue().toString());

                    stringBuilder.append("\n" + "/" + item.getKey() + "\n" + item.getValue()[1] + "\n");

                }



                replyTxt = stringBuilder.toString();

                break;

            case 주사위:

                long seed = System.currentTimeMillis();
                Random rand = new Random(seed);
                int iValue = rand.nextInt(6) + 1;

                replyTxt = "주사위= " + iValue;

                break;

        }

        sendReply(notification, replyTxt);
    }

    private void processRoom(Notification notification, COMMAND command, CharSequence roomName, List<String> parameter) {

        String replyTxt = "";
        String chatRoomName = null;

        if (roomName != null) {
            chatRoomName = roomName.toString();
        }
        
        if (chatRoomName == null) {
            sendReply(notification, "채팅방 멤버정보를 알 수 없습니다.");
            return;
        }


        switch (command) {

            case 뽑기:


                break;

            case 랜덤정렬:


                break;


        }

        sendReply(notification, replyTxt);
    }

    private void sendReply(Notification notification, String replyTxt) {

        replyTxt = replyTxt.trim();

        if (replyTxt.equals("")) return;

        for (Notification.Action action : notification.actions) {

            if (action != null && action.getRemoteInputs() != null) {
                RemoteInput[] remoteInputs = action.getRemoteInputs();
                for (RemoteInput remoteInput : remoteInputs) {

                    Log.d("remoteInput", remoteInput.getResultKey());

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence(remoteInput.getResultKey(), replyTxt);
                    RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent, bundle);

                    try {
                        action.actionIntent.send(getBaseContext(), 0, intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }

        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("NotificationListener", "[snowdeer] onNotificationRemoved() - " + sbn.toString());
    }

    enum COMMAND {
        명령어(COMMON, "명령어 리스트 보기"),
        주사위(COMMON, "1~6 사이 숫자 뽑기"),
        뽑기(ROOM, "/뽑기 (인원수) ex)/뽑기 2\n인원수 만큼 멤버 뽑기"),
        랜덤정렬(ROOM, "멤버 랜덤하게 순위정하기");

        private String[] value;

        COMMAND(String... value) {
            this.value = value;
        }

        public String getKey() {
            return name();
        }

        public String[] getValue() {
            return value;
        }
    }





}