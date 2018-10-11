package hw2;

import java.util.*;

public class Game {
    public static void main(String[] args) {
        int MaxNum;
        int LeftNum;
        
        int GameType;                               //1-移动，2-移除，3-添加
        int MoveNum;
        System.out.println("请输入游戏设置（以空格为间隔）:\n最大位数: >=1；等式左边数字个数: >=2；题目类型: 1-移动，2-移除，3-添加；变化的火柴棒根数: >=1");
        Scanner in = new Scanner(System.in);
        MaxNum = in.nextInt();
        LeftNum = in.nextInt();
        GameType = in.nextInt();
        MoveNum = in.nextInt();
        while(MoveNum>=4*LeftNum) {
            System.out.println("输入的变化火柴棒根数过多，请重新输入:");
            MaxNum = in.nextInt();
            LeftNum = in.nextInt();
            GameType = in.nextInt();
            MoveNum = in.nextInt();
        }
        
        System.out.println("题目：");
        Question Ques = new Question(MaxNum, LeftNum, GameType, MoveNum);
        Try(LeftNum, Ques, MaxNum);   
    }

    public static void Try(int LeftNum, Question Ques, int MaxNum) {
        boolean flag = false;
        boolean t = true;
        Scanner in = new Scanner(System.in);
        System.out.println("请输入答案：");
        String ans = in.nextLine();
        if(ans.isEmpty()) {
            Ques.RightAnswer();
            return;
        }
        String[] input_number_str =  ans.split("\\+|-|=");
        
        if(input_number_str.length==LeftNum+1) {
            int[] input_number = new int[LeftNum+1];
            for(int i=0;i<LeftNum+1;i++) {
                input_number[i] = Integer.parseInt(input_number_str[i]);
            }
            for(int i=0;i<LeftNum+1;i++) {
                if(input_number[i]>=Math.pow(10, MaxNum)) {
                    t = false;
                    break;
                }
            }
            int[] input_op = new int[LeftNum];
            input_op[0] = 1;
            int count = 1;
            for(int i=0;i<ans.length() && count<LeftNum;i++) {
                if(ans.charAt(i)=='+') {
                    input_op[count++] = 1;
                } else if(ans.charAt(i)=='-') {
                    input_op[count++] = -1;
                }
            }
            int sum = 0;
            for(int i=0;i<LeftNum;i++) {
                sum += input_op[i]*input_number[i];
            }
            if(sum==input_number[LeftNum] && t) {
                if(Ques.check(input_number, input_op) == true) {
                    flag = true;
                }
            }
        }
        
        while(flag==false) {
            t=true;
            System.out.println("答案错误，再试一次？（直接按回车显示正确答案）");
            ans = in.nextLine();
            if(ans.isEmpty()) {
                Ques.RightAnswer();
                return;
            }
            input_number_str =  ans.split("\\+|-|=");
            
            if(input_number_str.length==LeftNum+1) {
                int[] input_number = new int[LeftNum+1];
                for(int i=0;i<LeftNum+1;i++) {
                    input_number[i] = Integer.parseInt(input_number_str[i]);
                }
                for(int i=0;i<LeftNum+1;i++) {
                    if(input_number[i]>=Math.pow(10, MaxNum)) {
                        t = false;
                        break;
                    }
                }
                int[] input_op = new int[LeftNum];
                input_op[0] = 1;
                int count = 1;
                for(int i=0;i<ans.length() && count<LeftNum;i++) {
                    if(ans.charAt(i)=='+') {
                        input_op[count++] = 1;
                    } else if(ans.charAt(i)=='-') {
                        input_op[count++] = -1;
                    }
                }
                int sum = 0;
                for(int i=0;i<LeftNum;i++) {
                    sum += input_op[i]*input_number[i];
                }
                if(sum==input_number[LeftNum] && t) {
                    if(Ques.check(input_number, input_op) == true) {
                        flag = true;
                    }
                }
            }
        }

        System.out.println("恭喜，答案正确！");

        in.close();
    }
}

