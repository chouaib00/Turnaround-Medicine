package com.example.user.dipl1.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.List;

public class SpeechRecognitionHelper {

    public static void run(Activity ownerActivity) {
        // проверяем есть ли Activity для распознавания
        if (isSpeechRecognitionActivityPresented(ownerActivity) == true) {
            // если есть - запускаем распознавание
            startRecognition(ownerActivity);
        } else {
            // если нет, то показываем уведомление что надо установить Голосовой Поиск
            Toast.makeText(ownerActivity, "Чтобы активировать голосовой поиск необходимо установить \"Голосовой поиск Google\"", Toast.LENGTH_LONG).show();
            // начинаем процесс установки
            installGoogleVoiceSearch(ownerActivity);
        }
    }

    private static boolean isSpeechRecognitionActivityPresented(Activity ownerActivity) {
        try {
            // получаем экземпляр менеджера пакетов
            PackageManager pm = ownerActivity.getPackageManager();
            // получаем список Activity способных обработать запрос на распознавание
            List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

            if (activities.size() != 0) {	// если список не пустой
                return true;				// то умеем распознавать речь
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // не умеем распознавать речь
    }

    private static void startRecognition(Activity ownerActivity) {

        try {
            // создаем Intent с действием RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            // добавляем дополнительные параметры:
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Голосовой поиск");	// текстовая подсказка пользователю
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);	// модель распознавания оптимальная для распознавания коротких фраз-поисковых запросов
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);	// количество результатов, которое мы хотим получить, в данном случае хотим только первый - самый релевантный

            // стартуем Activity и ждем от нее результата
            ownerActivity.startActivityForResult(intent, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void installGoogleVoiceSearch(final Activity ownerActivity) {


        try {
            // создаем диалог, который спросит у пользователя хочет ли он
            // установить Голосовой Поиск
            Dialog dialog = new AlertDialog.Builder(ownerActivity)
                    .setMessage("Для распознавания речи необходимо установить \"Голосовой поиск Google\"")	// сообщение
                    .setTitle("Внимание")	// заголовок диалога
                    .setPositiveButton("Установить", new DialogInterface.OnClickListener() {	// положительная кнопка

                        // обработчик нажатия на кнопку Установить
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                // создаем Intent для открытия в маркете странички с приложением
                                // Голосовой Поиск имя пакета: com.google.android.voicesearch
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.voicesearch"));
                                // настраиваем флаги, чтобы маркет не попал к в историю нашего приложения (стек Activity)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                // отправляем Intent
                                ownerActivity.startActivity(intent);
                            } catch (Exception ex) {
                                // не удалось открыть маркет
                                // например из-за того что он не установлен
                                // ничего не поделаешь
                            }
                        }})

                    .setNegativeButton("Отмена", null)	// негативная кнопка
                    .create();

            dialog.show();	// показываем диалог
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
