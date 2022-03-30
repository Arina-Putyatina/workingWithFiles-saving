import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) {

        String mainFolder = "D:\\\\Games\\savegames";
        ArrayList<GameProgress> gameProgress = new ArrayList<>();

        gameProgress.add(new GameProgress(10, 3, 2, 55));
        gameProgress.add(new GameProgress(7, 1, 8, 456));
        gameProgress.add(new GameProgress(5, 8, 4, 112.5));

        int i = 1;
        ArrayList<String> gameProgressFiles = new ArrayList<>();
        for (GameProgress gp : gameProgress) {
            String fileName = mainFolder + "\\save" + i + ".dat";
            if (saveGame(fileName, gp)) {
                gameProgressFiles.add(fileName);
            }
            i++;
        }

        String zipName = mainFolder + "\\zip.zip";
        if (zipFiles(zipName, gameProgressFiles)) {
            deleteFiles(gameProgressFiles);
        }

        // Задача 3: Загрузка (со звездочкой *)
        ArrayList<String> files = openZip(zipName, mainFolder);
        if (files.size() > 0) {
            String fileName = files.get(0);
            GameProgress savedProgress = openProgress(fileName);
            if (savedProgress != null) {
                System.out.println(savedProgress);
            } else {
                System.out.println("Не удалось восстановить " + fileName);
            }
        }

    }

    public static boolean saveGame(String fileName, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public static boolean zipFiles(String zipName, ArrayList<String> files) {

        boolean res = true;

        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipName))) {
            for (String fileName : files) {
                try (FileInputStream fis = new FileInputStream(fileName)) {
                    File file = new File(fileName);
                    ZipEntry entry = new ZipEntry(file.getName());
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);

                    zout.closeEntry();
                } catch (Exception ex) {
                    res = false;
                    System.out.println(ex.getMessage());
                }

            }
        } catch (Exception ex) {
            res = false;
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public static void deleteFiles(ArrayList<String> files) {

        for (String fileName : files) {
            File file = new File(fileName);
            file.delete();
        }

    }

    public static ArrayList<String> openZip(String zipName, String folder) {

        ArrayList<String> files = new ArrayList<>();
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipName))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = folder + "\\" + entry.getName();
                files.add(name);
                FileOutputStream fout = new FileOutputStream(name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return files;
    }

    public static GameProgress openProgress(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            GameProgress progress = (GameProgress) ois.readObject();
            return progress;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
