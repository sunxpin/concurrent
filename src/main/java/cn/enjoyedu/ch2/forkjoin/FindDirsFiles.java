package cn.enjoyedu.ch2.forkjoin;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * 类说明：遍历指定目录（含子目录）找寻指定类型文件
 */
public class FindDirsFiles extends RecursiveAction {

    private File path;

    //            public static final String NAME = "完具m";
    public static final String NAME = "完具m_榨汁姬 ";
//    public static final String NAME = "完具m_娜美妖姬_榨汁姬 ";
//    public static final String NAME = "完具m_娜美妖姬";

    public static final String PIC = "PNG,png,jpg,jpeg,JPG,JPEG,BMP,bmp";
    public static final String VIDEO = "MP4,mp4";

    //    public static final String PIC_DEST = "E:/完具m_娜美妖姬 Collections/PIC/";
    public static final String PIC_DEST = "E:/榨汁姬-完具m-娜美妖姬/Pictures/";
    public static final String MV_DEST = "E:/完具m/MV/";

    private AtomicInteger count = new AtomicInteger(1);

    public FindDirsFiles(File path) {
        this.path = path;
    }

    @Override
    protected void compute() {
        List<FindDirsFiles> subTasks = new ArrayList<>();
        File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 对每个子目录都新建一个子任务。
                    subTasks.add(new FindDirsFiles(file));
                } else {
                    int lastIndexOf = file.getName().lastIndexOf(".");
                    // /获取文件的后缀名 .jpg
                    String suffix = file.getName().substring(lastIndexOf + 1);
                    // 遇到文件，检查。fun2048.com@.DS_Store
                    if (file.getName().startsWith("2048扫码获取新地址")) {
                        FileUtil.del(file);
                    } else if (PIC.contains(suffix)) {
//                        dealPictures(file);
//                        copyPictures(file);
                    } else if (VIDEO.contains(suffix)) {
//                        dealMV(file);
                        copyMV(file);
                    }
                }
            }
            if (!subTasks.isEmpty()) {
                // 在当前的 ForkJoinPool 上调度所有的子任务。
                for (FindDirsFiles subTask : invokeAll(subTasks)) {
                    subTask.join();
                }
            }
        }
    }

    private void copyMV(File file) {
        FileUtil.copy(file.getAbsolutePath(), MV_DEST + file.getName(), false);
    }

    private void copyPictures(File file) {
        FileUtil.copy(file.getAbsolutePath(), PIC_DEST + file.getName(), false);
    }

    private void dealMV(File file) {
        String[] split = file.getParent().split("\\\\");
        String newStr = split[split.length - 1];
        newStr = newStr.trim().replace(" ", "");
        String REGEX = "[^(0-9).]";
        String num = Pattern.compile(REGEX).matcher(newStr).replaceAll("").trim();
        if (num.length() >= 9) num = num.substring(0, 8);
        newStr = newStr.replaceAll("[^\u4E00-\u9FA5]", "");
        FileUtil.rename(file, NAME + num + " " + newStr + "_" + count.getAndIncrement(), true, false);

    }

    private void dealPictures(File file) {
        String[] split = file.getParent().split("\\\\");
        String newStr = split[split.length - 1];
        newStr = newStr.trim().replace(" ", "");
        if (newStr.contains("疑似") || newStr.contains("鬼知道")) {
            newStr = "";
        }
        String REGEX = "[^(0-9).]";
        String num = Pattern.compile(REGEX).matcher(newStr).replaceAll("").trim();
        if (num.length() >= 9) num = num.substring(0, 8);
        newStr = newStr.replaceAll("[^\u4E00-\u9FA5]", "");
        FileUtil.rename(file, NAME + num + "" + newStr + "_" + count.incrementAndGet(), true, false);
    }


    public static void main(String[] args) {
        try {
            // 用一个 ForkJoinPool 实例调度总任务
            ForkJoinPool pool = new ForkJoinPool();
//            FindDirsFiles task = new FindDirsFiles(new File("H:/完具m(娜美)_榨汁姬/"));
//            FindDirsFiles task = new FindDirsFiles(new File("F:/Download/[2018年11月-2019年03月]/"));
            FindDirsFiles task = new FindDirsFiles(new File("H:\\完具m"));

            /*异步提交*/
            pool.execute(task);

            /*主线程做自己的业务工作*/
            System.out.println("Task is Running......");
            Thread.sleep(1);
            int otherWork = 0;
            for (int i = 0; i < 100; i++) {
                otherWork = otherWork + i;
            }
            System.out.println("Main Thread done sth......,otherWork=" + otherWork);
            task.join();//阻塞方法
            System.out.println("Task end");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
