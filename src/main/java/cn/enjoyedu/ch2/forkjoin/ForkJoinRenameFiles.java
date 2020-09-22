package cn.enjoyedu.ch2.forkjoin;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinRenameFiles extends RecursiveAction {

    private File path;

    public static final String PIC = "PNG,png,jpg,jpeg,JPG,JPEG,BMP,bmp";

    public static final String VIDEO = "MP4,mp4";

    public ForkJoinRenameFiles(File path) {
        this.path = path;
    }

    @Override
    protected void compute() {
        List<ForkJoinRenameFiles> subTasks = new ArrayList<>();
        File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.listFiles().length == 0) {
                        file.delete();
                        continue;
                    }
                    subTasks.add(new ForkJoinRenameFiles(file));
                } else {
                    String name = file.getName();
                    if (name.startsWith("2048扫码获取新地址")) {
                        FileUtil.del(file);
                        continue;
                    }

                    String absolutePath = file.getAbsolutePath();
                    String[] split = absolutePath.split("\\\\");
                    String dest = null;
                    if (split.length - 1 > 5) {
                        Path path = file.toPath();
                        dest = path.getParent().getParent().toString() + File.separator + name;
                        FileUtil.copy(absolutePath, dest, true);
                        FileUtil.del(file);
                    }
                    if (dest != null)
                        FileUtil.rename(new File(dest), name.replace(".", "ArinaHashimoto"), false, true);
                    else
                        FileUtil.rename(file, name.replace(".", "ArinaHashimoto"), false, true);
                }
            }
            if (!subTasks.isEmpty()) {
                for (ForkJoinRenameFiles subTask : invokeAll(subTasks)) {
                    subTask.join();
                }
            }
        }
    }

    public static void main(String[] args) {
        String path = "D:\\doc\\完具m";
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinRenameFiles task = new ForkJoinRenameFiles(new File(path));
        pool.execute(task);
        System.out.println("Task is Running...");
        task.join();
        System.out.println("Task finished...");
    }
}
