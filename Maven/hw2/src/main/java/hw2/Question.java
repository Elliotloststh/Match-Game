package hw2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random; 
import java.lang.Math;

public class Question {
    int[] Numbers;      //等号左边数字
    int[][] digit;      //等号左边数字的单个阿拉伯数字
    int Sum;            //和
    int[] Sum_digit;
    int[] op;           //操作符,1为加号，-1为减号
    int MaxNum;
    int LeftNum;
    int GameType;
    int MoveNum;

    int[][] digit2;     //改变后的
    int[] Sum_digit2;
    int[] op2;

    int r=0;
    int differ[][] = {{ 6,-4, 0, 0, 0, 0, 0,-3, 1, 0},      //数字直接转化需要对火柴根数
                      { 4, 2, 0, 3, 2, 0, 4, 1, 5, 4},
                      { 0, 0, 5, 0, 0, 0, 0, 0, 2, 0},
                      { 0,-3, 0, 5, 0, 0, 0,-2, 2, 1},
                      { 0,-2, 0, 0, 4, 0, 0, 0, 3, 2},
                      { 0, 0, 0, 0, 0, 5, 1, 0, 2, 1},
                      { 0,-4, 0, 0, 0,-1, 6, 0, 1, 0},
                      { 3,-1, 0, 2, 0, 0, 0, 3, 4, 0},
                      {-1,-5,-2,-2,-3,-2,-1,-4, 7,-1},
                      { 0,-4, 0,-1,-2,-1, 0, 0, 1, 6}};

    int[] MatchNum = {6,2,5,5,4,5,6,3,7,6};         //数字的组成火柴棒个数

    int adjust[][] = {{0,0,1,1,1,1,1,0,0,1},        //数字转变需要的内部调整火柴根数
                      {0,0,1,0,0,1,0,0,0,0},
                      {1,1,0,1,2,2,1,1,0,1},
                      {1,0,1,0,1,1,1,0,0,0},
                      {1,0,2,1,0,1,1,1,0,0},
                      {1,1,2,1,1,0,0,1,0,0},
                      {1,0,1,1,1,0,0,1,0,1},
                      {0,0,1,0,1,1,1,0,0,0},
                      {0,0,0,0,0,0,0,0,0,0},
                      {1,0,1,0,0,0,1,0,0,0}};

    Question(int MaxNum1, int LeftNum1, int GameType1, int MoveNum1) {      //构造，申请内存
        MaxNum = MaxNum1;
        LeftNum = LeftNum1;
        GameType = GameType1;
        MoveNum = MoveNum1;
        Numbers = new int[LeftNum];
        digit = new int[LeftNum][MaxNum];
        Sum_digit = new int[MaxNum];
        op = new int[LeftNum];  //假定第一个数前有一个隐藏的+
        op[0] = 1;

        digit2 = new int[LeftNum][MaxNum];
        Sum_digit2 = new int[MaxNum];
        op2 = new int[LeftNum]; 
        op2[0] = 1;
        Create();       
    }

    public void Create() {                  //生成问题
        //首先生成一个正确的等式
        Sum = 0;
        Random random = new Random();
        int max = (int)Math.pow(10, MaxNum);
        for(int i=0;i<LeftNum;i++) {                //生成随机的数字与操作数相加
            Numbers[i] = random.nextInt(max);
        }
        for(int i=0;i<LeftNum-1;i++) {
            op[i+1] = 2*random.nextInt(2)-1;
        }
        for(int i=0;i<LeftNum;i++) {
            Sum+=(op[i] * Numbers[i]);
        }
        if(Sum>=max || Sum<0) {         //当和为负或超过位数限制，重新生成
            Create();
            return;
        }
        else {                                  //将数字拆分成单个阿拉伯数字，存储在数组中
            for(int i=0;i<LeftNum;i++) {
                for(int j=0;j<MaxNum;j++) {
                    digit[i][MaxNum-1-j] = ((int)(Numbers[i]/Math.pow(10, j)))%10;
                }
                int k = 0;
                while(digit[i][k]==0 && k<MaxNum-1) {
                    digit[i][k] = -1;
                    k++;
                }
            }
            for(int j=0;j<MaxNum;j++) {
                Sum_digit[MaxNum-1-j] = ((int)(Sum/Math.pow(10, j)))%10;
            }
            int k = 0;
            while(Sum_digit[k]==0 && k<MaxNum-1) {
                Sum_digit[k] = -1;
                k++;
            }
        }
        //根据游戏类型生成不同的题目
        if(GameType == 1) {
            CreateTransferGame();
        }
        else if(GameType == 2) {
            CreateDeleteGame();
        }
        else if(GameType == 3) {
            CreateAddGame();
        }
        
    }  

    //生成移动问题
    public void CreateTransferGame() {
        //用于存储每个数字加操作符可以通过移动变成其他数字的所有候选，链表顺序是先左边数字，再和，再操作符
        //如数字1，其hashmap结构为<0,{4,0}>,表示变成0需要增加四根，调整0根
        LinkedList<HashMap<Integer,LinkedList<Integer>>> Candidate = new LinkedList<HashMap<Integer,LinkedList<Integer>>>();

        //左边数字加入Candidate
        for(int i=0;i<LeftNum;i++) {
            for(int j=0;j<MaxNum;j++) {
                HashMap<Integer,LinkedList<Integer>> tmp = new HashMap<Integer,LinkedList<Integer>>();
                if(possible(3, i, j)) {
                    if(digit[i][j]==-1) {
                        for(int k=1;k<10;k++) {
                            if(MatchNum[k] <= MoveNum) {
                                LinkedList<Integer> tmp2 = new LinkedList<Integer>();
                                tmp2.add(MatchNum[k]);
                                tmp2.add(0);
                                tmp.put(k, tmp2);
                            }
                        } 
                    } else {
                        for(int k=0;k<10;k++) {
                            if(Math.abs(MatchNum[k]-MatchNum[digit[i][j]])+adjust[digit[i][j]][k] <= MoveNum && k!=digit[i][j]) {
                                LinkedList<Integer> tmp2 = new LinkedList<Integer>();
                                tmp2.add(MatchNum[k]-MatchNum[digit[i][j]]);
                                tmp2.add(adjust[digit[i][j]][k]);
                                tmp.put(k, tmp2);
                            }
                        }
                    }
                }
                Candidate.add(tmp);
            }
        }
        //和加入Candidate
        for(int j=0;j<MaxNum;j++) {
            HashMap<Integer,LinkedList<Integer>> tmp = new HashMap<Integer,LinkedList<Integer>>();
            if(possible(3, -1, j)) {                
                if(Sum_digit[j]==-1) {
                    for(int k=1;k<10;k++) {
                        if(MatchNum[k] <= MoveNum) {
                            LinkedList<Integer> tmp2 = new LinkedList<Integer>();
                            tmp2.add(MatchNum[k]);
                            tmp2.add(0);
                            tmp.put(k, tmp2);
                        }
                    } 
                } else {
                    for(int k=0;k<10;k++) {
                        if(Math.abs(MatchNum[k]-MatchNum[Sum_digit[j]])+adjust[Sum_digit[j]][k] <= MoveNum && k!=Sum_digit[j]) {
                            LinkedList<Integer> tmp2 = new LinkedList<Integer>();
                            tmp2.add(MatchNum[k]-MatchNum[Sum_digit[j]]);
                            tmp2.add(adjust[Sum_digit[j]][k]);
                            tmp.put(k, tmp2);
                        }
                    }
                }               
            }   
            Candidate.add(tmp);
        }
        //操作符加入Candidate，若能转换，hashmap的Key始终为1，表示变化了
        for(int i=1;i<LeftNum;i++) {
            HashMap<Integer,LinkedList<Integer>> tmp = new HashMap<Integer,LinkedList<Integer>>();
            if(op[i]==-1) {
                LinkedList<Integer> tmp2 = new LinkedList<Integer>();
                tmp2.add(1);
                tmp2.add(0);
                tmp.put(1, tmp2);
            } else {
                LinkedList<Integer> tmp2 = new LinkedList<Integer>();
                tmp2.add(-1);
                tmp2.add(0);
                tmp.put(1, tmp2);
            }
            Candidate.add(tmp);
        }

        //储存所有可能的问题，由于回溯的深度可能不一样，用链表表示。链表元素依次表示左边数，和，操作符的变化，不变则为-1，无节点也为不变。
        LinkedList<LinkedList<Integer>> res = new LinkedList<LinkedList<Integer>>();
        BackTrack2(Candidate, res, new LinkedList<Integer>(), 0, 0, MoveNum, MoveNum);

        for(int i=0;i<res.size();i++) {                                         //去除首位为0
            for(int j=0;j<LeftNum+1 &&  MaxNum*j<res.get(i).size();j++) {
                if(res.get(i).get(MaxNum*j) == 0) {
                    res.remove(i);
                    i--;break;
                }
            }
        }
        
        //----------------give question---------------//
        if(res.size()==0) {
            Create();
            r++;
            return;
        }
        Random random = new Random();
        int chosen_index = random.nextInt(res.size());          //从res中随机选择一个给出问题
        GiveQues(res.get(chosen_index));
    }

    //生成移除问题
    public void CreateDeleteGame() {
        //与移动问题类似，只是Candidate的结构简单一点，因为不像移动有加减与调整两个过程，这里只有直接转化。
        LinkedList<HashMap<Integer ,Integer>> Candidate = new LinkedList<HashMap<Integer ,Integer>>();
        for(int i=0;i<LeftNum;i++) {
            for(int j=0;j<MaxNum;j++) {
                HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
                if(possible(2, i, j)) {                   
                    if(digit[i][j]==-1) {
                        for(int k=1;k<10;k++) {
                            if(MatchNum[k] <= MoveNum) {
                                tmp.put(k, MatchNum[k]);
                            }
                        } 
                    } else {
                        for(int k=0;k<10;k++) {
                            if(differ[digit[i][j]][k] <= MoveNum && differ[digit[i][j]][k] > 0 && k!=digit[i][j]) {
                                tmp.put(k, differ[digit[i][j]][k]);
                            }
                        }
                    }                
                }   
                Candidate.add(tmp);
            }
        }
        for(int j=0;j<MaxNum;j++) {
            HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
            if(possible(2, -1, j)) {                
                if(Sum_digit[j]==-1) {
                    for(int k=1;k<10;k++) {
                        if(MatchNum[k] <= MoveNum) {
                            tmp.put(k, MatchNum[k]);
                        }
                    } 
                } else {
                    for(int k=0;k<10;k++) {
                        if(differ[Sum_digit[j]][k] <= MoveNum && differ[Sum_digit[j]][k] > 0 && k!=Sum_digit[j]) {
                            tmp.put(k, differ[Sum_digit[j]][k]);
                        }
                    }
                }               
            }   
            Candidate.add(tmp);
        }
        for(int i=1;i<LeftNum;i++) {
            HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
            if(op[i]==-1) {
                tmp.put(1, 1);
            }
            
            Candidate.add(tmp);
        }

        LinkedList<LinkedList<Integer>> res = new LinkedList<LinkedList<Integer>>();
        BackTrack(Candidate, res, new LinkedList<Integer>(), 0, 0, MoveNum);
        
        for(int i=0;i<res.size();i++) {                                         //去除首位为0
            for(int j=0;j<LeftNum+1 &&  MaxNum*j<res.get(i).size();j++) {
                if(res.get(i).get(MaxNum*j) == 0) {
                    res.remove(i);
                    i--;break;
                }
            }
        }
        
        //----------------give question---------------//
        if(res.size()==0) {
            Create();
            r++;
            return;
        }
        Random random = new Random();
        int chosen_index = random.nextInt(res.size());
        GiveQues(res.get(chosen_index));


    }

    //生成添加问题
    public void CreateAddGame() {
        //与移动问题类似，只是Candidate的结构简单一点，因为不像移动有加减与调整两个过程，这里只有直接转化。
        LinkedList<HashMap<Integer ,Integer>> Candidate = new LinkedList<HashMap<Integer ,Integer>>();
        for(int i=0;i<LeftNum;i++) {
            for(int j=0;j<MaxNum;j++) {
                HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
                if(possible(3, i, j)) {                   
                    for(int k=0;k<10;k++) {
                        if(-differ[digit[i][j]][k] <= MoveNum && differ[digit[i][j]][k] < 0 && k!=digit[i][j]) {
                            tmp.put(k, -differ[digit[i][j]][k]);
                        }
                    }               
                }   
                Candidate.add(tmp);
            }
        }
        for(int j=0;j<MaxNum;j++) {
            HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
            if(possible(3, -1, j)) {                
                for(int k=0;k<10;k++) {
                    if(-differ[Sum_digit[j]][k] <= MoveNum && differ[Sum_digit[j]][k] < 0 && k!=Sum_digit[j]) {
                        tmp.put(k, -differ[Sum_digit[j]][k]);
                    }
                }               
            }   
            Candidate.add(tmp);
        }
        for(int i=1;i<LeftNum;i++) {
            HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
            if(op[i]==1) {
                tmp.put(1, 1);
            }
            
            Candidate.add(tmp);
        }

        LinkedList<LinkedList<Integer>> res = new LinkedList<LinkedList<Integer>>();
        BackTrack(Candidate, res, new LinkedList<Integer>(), 0, 0, MoveNum);
        
        for(int i=0;i<res.size();i++) {                                         //去除首位为0
            for(int j=0;j<LeftNum+1 &&  MaxNum*j<res.get(i).size();j++) {
                if(res.get(i).get(MaxNum*j) == 0) {
                    res.remove(i);
                    i--;break;
                }
            }
        }
        
        //----------------give question---------------//
        if(res.size()==0) {
            Create();
            r++;
            return;
        }
        Random random = new Random();
        int chosen_index = random.nextInt(res.size());
        GiveQues(res.get(chosen_index));
    }

    //根据选中的res改变正确等式，给出问题
    private void GiveQues(LinkedList<Integer> list) {
        for(int i=0;i<MaxNum*LeftNum;i++) {
            int r = (int)(i/MaxNum);
            if(i<list.size()) {
                if(list.get(i)!=-1) {              
                    digit2[r][i-r*MaxNum] = list.get(i);
                }else {
                    digit2[r][i-r*MaxNum] = digit[r][i-r*MaxNum];
                }
            }else {
                digit2[r][i-r*MaxNum] = digit[r][i-r*MaxNum];
            }
        }
        for(int i=0;i<MaxNum;i++) {
            if(i+MaxNum*LeftNum<list.size()) {
                if(list.get(i+MaxNum*LeftNum)!=-1) {
                    Sum_digit2[i] = list.get(i+MaxNum*LeftNum);
                }else {
                    Sum_digit2[i] = Sum_digit[i];
                }
            }else {
                Sum_digit2[i] = Sum_digit[i];
            }
        }
        for(int i=0;i<LeftNum-1;i++) {
            if(i+MaxNum*LeftNum+MaxNum<list.size()) {
                if(list.get(i+MaxNum*LeftNum+MaxNum)!=-1) {
                    op2[i+1] = -op[i+1];
                }else {
                    op2[i+1] = op[i+1];
                }
            }else {
                op2[i+1] = op[i+1];
            }
        }
        for(int i=0;i<LeftNum;i++) {
            if(i!=0) {
                System.out.print((op2[i]==1)?"+":"-");
            }       	
            for(int j=0;j<MaxNum;j++) {
                if(digit2[i][j]!=-1) {
                    System.out.print(digit2[i][j]);
                }              
            }
            
        }
        System.out.print("=");
        for(int j=0;j<MaxNum;j++) {
            if(Sum_digit2[j]!=-1) {
                System.out.print(Sum_digit2[j]);
            }
        }
        System.out.println();

    }

    //回溯获得所有可以给出的题目,BackTrack2是移动模式，BackTrack为移除或添加模式
    private void BackTrack2(LinkedList<HashMap<Integer ,LinkedList<Integer>>> Candidate, LinkedList<LinkedList<Integer>> res,    
                            LinkedList<Integer> path, int index1, int index2, int target1, int target2) {
        int tmp = index2;
        
        if(target1 == 0 && target2 == 0) {
            res.add(new LinkedList<Integer>(path));              
            return;
        }
        for(int i=index1;i<Candidate.size();i++) {
            index2 = 0;
            tmp = index2;
            for(Map.Entry<Integer,LinkedList<Integer>> entry: Candidate.get(i).entrySet()) {              
                if(tmp>0) {
                    tmp--;
                    continue;
                }               
                if(entry.getValue().get(0)>=0) {
                    if(entry.getValue().get(0)+entry.getValue().get(1) <= target1 && entry.getValue().get(1) <= target2) {
                        path.add(entry.getKey());
                        BackTrack2(Candidate, res, path, i+1, 0, target1-entry.getValue().get(0)-entry.getValue().get(1),
                                        target2-entry.getValue().get(1));
                        while(i<path.size()) {
                        path.remove(path.size()-1);  
                        } 
                    }                                                 
                } else {
                    if(-entry.getValue().get(0)+entry.getValue().get(1) <= target2 && entry.getValue().get(1)<=target1) {
                        path.add(entry.getKey());
                        BackTrack2(Candidate, res, path, i+1, 0, target1-entry.getValue().get(1),
                                        target2+entry.getValue().get(0)-entry.getValue().get(1));
                        while(i<path.size()) {
                        path.remove(path.size()-1);  
                        } 
                    }                                                 
                }
                index2++;
            }
            path.add(-1);          
        }
    }

    private void BackTrack(LinkedList<HashMap<Integer ,Integer>> Candidate, LinkedList<LinkedList<Integer>> res,    
                            LinkedList<Integer> path, int index1, int index2, int target) {
        int tmp = index2;
        if(target == 0) {
            res.add(new LinkedList<Integer>(path));                    
            return;
        }
        for(int i=index1;i<Candidate.size();i++) {
            index2 = 0;
            tmp = index2;
            for(Map.Entry<Integer,Integer> entry: Candidate.get(i).entrySet()) {              
                if(tmp>0) {
                    tmp--;
                    continue;
                }               
                if(entry.getValue()<=target) {
                    path.add(entry.getKey());
                    BackTrack(Candidate, res, path, i+1, 0, target-entry.getValue());
                    while(i<path.size()) {
                        path.remove(path.size()-1);  
                    }                               
                }
                index2++;
            }
            path.add(-1);          
        }
    }

    //用于判断某个阿拉伯数字是否可能进行转换
    public boolean possible(int GameType, int i, int j) {
        if(GameType == 2) {
            if(i==-1) {
                if(Sum_digit[j]==-1) {
                    if(MoveNum >= 2) {
                        return true;
                    } else {
                        return false;
                    }
                }
                for(int k=0;k<10;k++) {
                    if(differ[Sum_digit[j]][k] <= MoveNum && differ[Sum_digit[j]][k] > 0 && k!=Sum_digit[j]) {
                        return true;
                    }
                }
                return false;
            }
            if(digit[i][j]==-1) {
                if(MoveNum >= 2) {
                    return true;
                } else {
                    return false;
                }
            }
            for(int k=0;k<10;k++) {
                if(differ[digit[i][j]][k] <= MoveNum && differ[digit[i][j]][k] > 0 && k!=digit[i][j]) {
                    return true;
                }
            }           
            return false;
        }else if(GameType == 3) {
            if(i==-1) {
                if(Sum_digit[j]==-1) {
                    return false;
                }
                for(int k=0;k<10;k++) {
                    if(-differ[Sum_digit[j]][k] <= MoveNum && differ[Sum_digit[j]][k] < 0 && k!=Sum_digit[j]) {
                        return true;
                    }
                }
                return false;
            }
            if(digit[i][j]==-1) {
                return false;
            }
            for(int k=0;k<10;k++) {
                if(-differ[digit[i][j]][k] <= MoveNum && differ[digit[i][j]][k] < 0 && k!=digit[i][j]) {
                    return true;
                }
            }          
            return false;
        }else {
            if(i==-1) {
                if(Sum_digit[j]==-1) {
                    if(MoveNum >= 2) {
                        return true;
                    }else {
                        return false;
                    }
                }
                for(int k=0;k<10;k++) {
                    if(Math.abs(MatchNum[k]-MatchNum[Sum_digit[j]])+adjust[Sum_digit[j]][k] <= MoveNum && k!=Sum_digit[j]) {
                        return true;
                    }
                }
                return false;
            }
            if(digit[i][j]==-1) {
                if(MoveNum >= 2) {
                    return true;
                }else {
                    return false;
                }
            }
            for(int k=0;k<10;k++) {
                if(Math.abs(MatchNum[k]-MatchNum[digit[i][j]])+adjust[digit[i][j]][k] <= MoveNum && k!=digit[i][j]) {
                    return true;
                }
            }
            return false;

        }
        
    }

    //检验用户输入，正确返回true
    public boolean check(int[] input_number, int[] my_op) {
        int[][] my_digit = new int[LeftNum][MaxNum];
        int[] my_Sum_digit = new int[MaxNum];
        for(int i=0;i<LeftNum;i++) {
            for(int j=0;j<MaxNum;j++) {
                my_digit[i][MaxNum-1-j] = ((int)(input_number[i]/Math.pow(10, j)))%10;
            }
            int k = 0;
            while(my_digit[i][k]==0 && k<MaxNum-1) {
                my_digit[i][k] = -1;
                k++;
            }
        }
        for(int j=0;j<MaxNum;j++) {
            my_Sum_digit[MaxNum-1-j] = ((int)(input_number[LeftNum]/Math.pow(10, j)))%10;
        }
        int k = 0;
        while(Sum_digit[k]==0 && k<MaxNum-1) {
            my_Sum_digit[k] = -1;
            k++;
        }
        
        //add表示实际添加的根数，sub表示实际移除的根数。求出后与输入的MoveNum进行比较即可得出判断，移动游戏还要看add与sub是否相等
        int add = 0;
        int sub = 0;
        //分别对左边数字，和，操作符进行检查
        if(GameType==2) {
            for(int i=0;i<LeftNum;i++) {
                for(int j=0;j<MaxNum;j++) {
                    if(digit2[i][j]==my_digit[i][j]) {
                        continue;
                    } else if(digit2[i][j]==-1) {
                        return false;
                    } else if(my_digit[i][j]==-1) {
                        sub+=MatchNum[digit2[i][j]];
                    } else {
                        if(differ[digit2[i][j]][my_digit[i][j]]<0) {
                            sub += -differ[digit2[i][j]][my_digit[i][j]];
                        }else {
                            return false;
                        }
                    }
                }
            }
            for(int j=0;j<MaxNum;j++) {
                if(Sum_digit2[j]==my_Sum_digit[j]) {
                    continue;
                } else if(Sum_digit2[j]==-1) {
                    return false;
                } else if(my_Sum_digit[j]==-1) {
                    sub+=MatchNum[Sum_digit2[j]];
                } else {
                    if(differ[Sum_digit2[j]][my_Sum_digit[j]]<0) {
                        sub += -differ[Sum_digit2[j]][my_Sum_digit[j]];
                    }else {
                        return false;
                    }
                }
            }
            for(int i=1;i<LeftNum;i++) {
                if(op2[i]==1&&my_op[i]==-1) {
                    sub+=1;
                }else if(op2[i]==-1&&my_op[i]==1) {
                    return false;
                }
            }
            if(sub==MoveNum) {
                return true;
            } else {
                return false;
            }
        } else if(GameType==3) {
            for(int i=0;i<LeftNum;i++) {
                for(int j=0;j<MaxNum;j++) {
                    if(digit2[i][j]==my_digit[i][j]) {
                        continue;
                    } else if(digit2[i][j]==-1) {
                        add+=MatchNum[my_digit[i][j]];  
                    } else if(my_digit[i][j]==-1) {
                        return false;
                    } else {
                        if(differ[digit2[i][j]][my_digit[i][j]]>0) {
                            add += differ[digit2[i][j]][my_digit[i][j]];
                        }else {
                            return false;
                        }
                    }
                }
            }
            for(int j=0;j<MaxNum;j++) {
                if(Sum_digit2[j]==my_Sum_digit[j]) {
                    continue;
                } else if(Sum_digit2[j]==-1) {
                    add+=MatchNum[my_Sum_digit[j]];
                } else if(my_Sum_digit[j]==-1) {
                    return false;
                } else {
                    if(differ[Sum_digit2[j]][my_Sum_digit[j]]>0) {
                        add += differ[Sum_digit2[j]][my_Sum_digit[j]];
                    }else {
                        return false;
                    }
                }
            }
            for(int i=1;i<LeftNum;i++) {
                if(op2[i]==1&&my_op[i]==-1) {
                    return false;
                }else if(op2[i]==-1&&my_op[i]==1) {
                    add+=1;
                }
            }
            //得出结果，判断是否正确
            if(add==MoveNum) {
                return true;
            } else {
                return false;
            }
        } else {
            for(int i=0;i<LeftNum;i++) {
                for(int j=0;j<MaxNum;j++) {
                    if(digit2[i][j]==my_digit[i][j]) {
                        continue;
                    } else if(digit2[i][j]==-1) {
                        add+=MatchNum[my_digit[i][j]];  
                    } else if(my_digit[i][j]==-1) {
                        sub+=MatchNum[digit2[i][j]];
                    } else {
                        if(MatchNum[my_digit[i][j]]-MatchNum[digit2[i][j]]>=0) {
                            add += MatchNum[my_digit[i][j]]-MatchNum[digit2[i][j]];
                        }else {
                            sub += -MatchNum[my_digit[i][j]]+MatchNum[digit2[i][j]];
                        }
                        add+=adjust[digit2[i][j]][my_digit[i][j]];
                        sub+=adjust[digit2[i][j]][my_digit[i][j]];
                    }
                }
            }
            for(int j=0;j<MaxNum;j++) {
                if(Sum_digit2[j]==my_Sum_digit[j]) {
                    continue;
                } else if(Sum_digit2[j]==-1) {
                    add+=MatchNum[my_Sum_digit[j]];
                } else if(my_Sum_digit[j]==-1) {
                    sub+=MatchNum[Sum_digit2[j]];
                } else {
                    if(MatchNum[my_Sum_digit[j]]-MatchNum[Sum_digit2[j]]>=0) {
                        add += MatchNum[my_Sum_digit[j]]-MatchNum[Sum_digit2[j]];
                    }else {
                        sub+= -MatchNum[my_Sum_digit[j]]+MatchNum[Sum_digit2[j]];
                    }
                    add+=adjust[Sum_digit2[j]][my_Sum_digit[j]];
                    sub+=adjust[Sum_digit2[j]][my_Sum_digit[j]];
                }
            }
            for(int i=1;i<LeftNum;i++) {
                if(op2[i]==1&&my_op[i]==-1) {
                    sub+=1;
                }else if(op2[i]==-1&&my_op[i]==1) {
                    add+=1;
                }
            }
            if(add==sub && add==MoveNum) {
                return true;
            } else {
                return false;
            }
        }
    }

    //输出预设的正确答案
    public  void RightAnswer() {
        System.out.println("正确答案：");
        for(int i=0;i<LeftNum;i++) {
            if(i!=0) {
                System.out.print((op[i]==1)?"+":"-");
            }       	
            for(int j=0;j<MaxNum;j++) {
                if(digit[i][j]!=-1) {
                    System.out.print(digit[i][j]);
                }              
            }
            
        }
        System.out.print("=");
        for(int j=0;j<MaxNum;j++) {
            if(Sum_digit[j]!=-1) {
                System.out.print(Sum_digit[j]);
            }
        }
        System.out.println();       
    }
}