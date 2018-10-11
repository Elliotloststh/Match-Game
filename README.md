## 一、介绍

火柴棒游戏

– 1.用户从命令行输入最大数字的位数(如1位数、2位数、3位数);
– 2.用户从命令行输入提示数(2或3)，表示等号左式数字的个数;
– 3.用户从命令行输入题目类型编号(移动、移除、添加)，以及 火柴棒根数;
– 3.系统随机自动生成火柴棒游戏，并展示(直接用数字展示);
– 4.用户输入答案，系统验证是否正确;若正确，则提示正确;若错误，则让用户继续输入;
– 5.若用户直接回车，则显示正确答案。

这里，我完成的功能是N位最大数字，N位提示数，但不支持负数。需要提醒的是，若移动火柴棒数字过大，生成题目可能会比较慢。

## 二、实验环境

操作系统：MacOS

实验平台：VS Code+Terminal

## 三、解决思路

我们把一个等式可以分为几个部分，一是左边的数字，二是和的数字，三是操作符（不包括=）。但一个数字无法用火柴棒很好地表示，于是我们将每一个数字拆成单个的阿拉伯数字，左边数字用二维数组表示，和用一维数组，其中部分位若不存在，如最大3位数，22的百位不存在，那一位用-1表示。操作符也同样用数组表示：1为加，-1为减。如最大位2，22+3=25表示为

```java
[2][2]     与   [2][5]   与   [1]

[-1][3]
```

首先第一步，我们需要生成一个随机的问题，关键的一点是我们应先生成正确的等式，然后进行加减操作变成新的错误的等式，而不是直接生成一个错误的等式。难点之一是要做到真正的随机性，生成正确等式的随机性是可以保证的，但由正确到错误的过程会比较难，我这里采用的方法是得出所有可能的错误等式，如移动1根，那我求出所有移动一根后仍能成为一个等式的求出来，删去其中仍然正确的，再随机取一个即可，这一步用到了回溯算法，下一节会讲到。因此当移动火柴棒数字较大时会消耗较长时间，但实际上这种火柴棒问题动三四根火柴棒已经是较大的数字了，所以也没有关系。

生成随机问题后验证答案就是个比较容易的过程了，将每个阿拉伯数字与操作符进行比较，看是否满足要求且答案等式正确即可（因为一个问题有多个解，所以不能直接直接和一开始生成的正确等式比较）。而用户输入回车看到的正确答案就是一开始生成的正确等式。

## 四、数据结构与算法

### 4.1 转化关系

0～9数字之间的转化，可以分为两种情况。

1.对于移除与添加，有确定的关系，在这里我用一个二维数组表示这种关系：

```java
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

```

其中`differ[i][j]`（i!=j）表示从数字i到数字j通过直接的添加移除需要的火柴棒根数，正为添加，负为移除，0表示无法转换。

2.对于移动的情况，任何数字之后都可以通过先添加或减少火柴棒根数，再移动调整的情况转化，如2到6，先增加一根火柴棒，再移动一根火柴棒即可。我定义了两组数组表示这种关系：

```java
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
```

其中，`MatchNum[i]`表示组成数字i的火柴棒根数，可以用来计算第一个过程所要的火柴棒，同样以2到6为例，即是 `MatchNum[6]-MatchNum[2]`为1；`adjust[i][j]`表示第二个过程需要的用于调整的火柴棒根数，`adjust[2][6]`为1。

然后当我们通过随机数生成一个正确等式后，我们根据题目类型，增加，减少，移动火柴棒获得新的错误等式。下面以最难的移动游戏为例。

### 4.2 第一步

```java
LinkedList<HashMap<Integer,LinkedList<Integer>>> Candidate = new LinkedList<HashMap<Integer,LinkedList<Integer>>>();
```

用于存储每个数字加操作符可以通过移动变成其他数字的所有候选，链表顺序是先左边数字，再和，再操作符。如对等式1+2=3，链表是1的map，2的map，3的map，+的map。HashMap存储结构为<0,{4.0}>,表示变成0需要增加四根，调整0根，<2,{3,1}>，表示变成2需要增加3根，调整1根，等等。我们通过对转化关系的分析可以很快得到结果。

### 4.3 回溯

```java
LinkedList<LinkedList<Integer>> res = new LinkedList<LinkedList<Integer>>();
```

储存所有可能的问题，由于回溯的深度可能不一样，第二层也用链表表示。链表元素依次表示左边数，和，操作符的变化，不变则为-1，变则为变成的那个数字，无节点也表示不变。

```java
private void BackTrack2(LinkedList<HashMap<Integer ,LinkedList<Integer>>> Candidate, LinkedList<LinkedList<Integer>> res, LinkedList<Integer> path, int index1, int index2, int target1, int target2)
```

这个函数用来回溯生成所有可能的解，移动问题实际上是添加问题加移除问题，只是需要两者火柴棒数一样而已。target1与target2表示还需要添加或移除的火柴棒。index1表示第几个数字（操作符），index2表示这个数字（操作符）的第几种可能的转化情况，path表示一个解。调用方式：

```java
BackTrack2(Candidate, res, new LinkedList<Integer>(), 0, 0, MoveNum, MoveNum);
```

下面分析一下回溯函数的算法，伪代码如下：

```java
private void BackTrack2(LinkedList<HashMap<Integer ,LinkedList<Integer>>> Candidate, LinkedList<LinkedList<Integer>> res, LinkedList<Integer> path, int index1, int index2, int target1, int target2) {
        if(target1 == 0 && target2 == 0) {
            res.add(new LinkedList<Integer>(path));              
            return;
        }
        for(each Candidate) {
            index2 = 0;
            for(each possibility  in this Candidate) {                           
                if(it can be added to path) {
                    path.add();
                    BackTrack2(Candidate, res, path, new index, new target);
  					path.remove();  
                                                 
                } 
                index2++;
            }
            path.add(-1);          
        }
    }
```

回溯的终点之一是当target1与target2都为0，此时拷贝一个path加入res中。其他就是对每一个数字遍历其可能转化（可加入path，根据hashmap中的值与target比较可得出），然后更新target，进入下一层回溯，这个更新需要对转化需要加还是减火柴棒进行分类，较为复杂，具体可看源代码。回溯返回之后要清除回溯添加到path的possibility。若不存在可能的转化，path添加-1，返回，这是回溯不成功的终点。

添加与移除的回溯数据结构与函数简单一些，但大致都差不多，这里不再赘述了。

### 4.4 给出问题，检查答案

此时，res中已存储了所有可能的新等式，一个path中依次是左边数字、和、操作符转化成的新数，为空时默认不变。我们随机取一个，遍历得到新的等式。之后用户输入答案，我们调用Question.check函数，一个个比较数字与操作符，看是否与条件相符即可。

## 五、测试结果

测试移动：

![image-20181008183319200](/Users/elliot/Library/Application Support/typora-user-images/image-20181008183319200.png)

测试移动：

![573A28B0-96DD-4575-B280-E8E0E16D636D](/Users/elliot/Library/Containers/com.tencent.qq/Data/Library/Application Support/QQ/Users/1195603488/QQ/Temp.db/573A28B0-96DD-4575-B280-E8E0E16D636D.png)

测试移除：

![53836949-8927-4B37-8D61-6187863C4854](/Users/elliot/Library/Containers/com.tencent.qq/Data/Library/Application Support/QQ/Users/1195603488/QQ/Temp.db/53836949-8927-4B37-8D61-6187863C4854.png)

测试添加：

![232B8895-3FE5-43D5-8260-CBADC52D5865](/Users/elliot/Library/Containers/com.tencent.qq/Data/Library/Application Support/QQ/Users/1195603488/QQ/Temp.db/232B8895-3FE5-43D5-8260-CBADC52D5865.png)

经测试均符合预期，部分测试截图如上。

