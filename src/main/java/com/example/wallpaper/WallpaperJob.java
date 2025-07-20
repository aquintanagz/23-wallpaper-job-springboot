package com.example.wallpaper;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Random;
import java.nio.file.*;

@Component
public class WallpaperJob {

    private static final String WALLPAPER_DIR = "C:\\Lapras\\PUERTAS\\PROYECTOS\\23_wallpaper-job-springboot\\wallpaper-job-springboot\\changeWallpaper"; // cambia según tu ruta
    private static final String SCRIPT_PATH = "C:\\Lapras\\PUERTAS\\HER\\23_bin\\change_wallpaper.bat";

    @Scheduled(fixedRate = 3600000) // cada hora
    public void cambiarFondo() {
        try {
            String[] wallpapers = new File(WALLPAPER_DIR).list((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".bmp");
            });

            if (wallpapers == null || wallpapers.length == 0) {
                System.out.println("No se encontraron imágenes.");
                return;
            }

            String selected = wallpapers[new Random().nextInt(wallpapers.length)];
            String fullPath = WALLPAPER_DIR + "/" + selected;

            Files.write(Paths.get(SCRIPT_PATH), (
                "@echo off
" +
                "reg add "HKCU\Control Panel\Desktop" /v Wallpaper /t REG_SZ /d "" + fullPath + "" /f
" +
                "RUNDLL32.EXE user32.dll,UpdatePerUserSystemParameters
"
            ).getBytes());

            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", SCRIPT_PATH);
            pb.inheritIO();
            pb.start();

            System.out.println("Fondo cambiado a: " + fullPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
