package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import domain.Lyric;

/**
 * Created by xinpengfei on 2016/10/10.
 * <p>
 * Function :解析歌词
 */

public class LyricUtils {

    private boolean isExistsLyric = false;
    private ArrayList<Lyric> lyrics;

    /**
     * 是否存在歌词
     *
     * @return
     */
    public boolean isExistsLyric() {
        return isExistsLyric;
    }

    public void readLyricFile(File file) {

        if (file == null || !file.exists()) {
            //歌词文件不存在
            lyrics = null;
            isExistsLyric = false;
        } else {
            //歌词文件存在--->解析歌词
            isExistsLyric = true;
            lyrics = new ArrayList<>();
            //2.读取歌词文件的每一行
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    line = parseLyric(line);//解析每一句
                }
                //全部解析完成
            } catch (Exception e) {
                e.printStackTrace();
            }
            //3.排序
            Collections.sort(lyrics, new Mysort());
            //4.计算每句歌词高亮的时间
            for (int i = 0; i < lyrics.size(); i++) {
                //取出前一句
                Lyric onelyric = lyrics.get(i);
                if (i + 1 < lyrics.size()) {
                    Lyric twoLyric = lyrics.get(i + 1);
                    onelyric.setSleepTime(twoLyric.getTimePoint() - onelyric.getTimePoint());
                }
            }
        }

    }

    /**
     * 得到歌词列表
     *
     * @return
     */
    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }

    /**
     * 解析每句歌词
     *
     * @param line : [02:04.12][03:37.32][00:59.73]我在这里欢笑
     * @return
     */
    private String parseLyric(String line) {

        int pos1 = line.indexOf("[");//第一次出现[的位置，0，如果没有找到返回-1
        int pos2 = line.indexOf("]");//9
        if (pos1 == 0 && pos2 != -1) {//至少有一句歌词

            long[] timeLong = new long[getCountTag(line)];

            String strTime = line.substring(pos1 + 1, pos2);//02:04.12
            timeLong[0] = stringTimeToLong(strTime);//02:04.12-->long类型（毫秒）

            if (timeLong[0] == -1) {
                return "";
            }

            int i = 1;
            String content = line;//[02:04.12][03:37.32][00:59.73]我在这里欢笑
            while (pos1 == 0 && pos2 != -1) {
                content = content.substring(pos2 + 1);//[03:37.32][00:59.73]我在这里欢笑-->[00:59.73]我在这里欢笑--->我在这里欢笑
                pos1 = content.indexOf("[");//0-->-1
                pos2 = content.indexOf("]");//9-->-1

                if (pos2 != -1) {
                    strTime = content.substring(pos1 + 1, pos2);//03:37.32-->00:59.73
                    timeLong[i] = stringTimeToLong(strTime);//03:37.32-->long类型（毫秒） -->00:59.73

                    if (timeLong[i] == -1) {
                        return "";
                    }

                    i++;//2

                }

            }


            Lyric lyric = new Lyric();
            //时间戳和歌词内容关联起来
            for (int j = 0; j < timeLong.length; j++) {

                if (timeLong[j] != 0) {
                    lyric.setTimePoint(timeLong[j]);
                    lyric.setContent(content);

                    //添加到集合中
                    lyrics.add(lyric);
                    //重新创建
                    lyric = new Lyric();
                }

            }

            return content;
        }
        return "";
    }

    private int getCountTag(String line) {

        int result = 1;
        String[] left = line.split("\\[");
        String[] right = line.split("\\]");
        if (left.length == 0 && right.length == 0) {
            result = 1;
        } else if (left.length > right.length) {
            result = left.length;
        } else {
            result = right.length;
        }

        return result;
    }

    private long stringTimeToLong(String strTime) {

        long result = -1;
        try {
            //1.把02:04.12安照":"切成02 和04.12
            String[] s1 = strTime.split(":");
            //2.把04.12安照“.”切割成04和12
            String[] s2 = s1[1].split("\\.");
            //3.转换成long并且转换成毫秒

            //分
            long min = Long.parseLong(s1[0]);

            //秒
            long second = Long.parseLong(s2[0]);

            //毫秒
            long mil = Long.parseLong(s2[1]);

            result = min * 60 * 1000 + second * 1000 + mil * 10;

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return result;
    }


    class Mysort implements java.util.Comparator<Lyric> {

        @Override
        public int compare(Lyric o1, Lyric o2) {

            if (o1.getTimePoint() < o2.getTimePoint()) {
                return -1;
            } else if (o1.getTimePoint() > o2.getTimePoint()) {
                return 1;
            } else {

                return 0;
            }

        }
    }
}
