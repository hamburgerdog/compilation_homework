package com.xjosiah.grammer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 一个算式表达式LL(1)分析表生成器；
 *
 * @since 2020.12.7
 */
public class Grammer {
    private String[][] analyzeTable;

    private ArrayList<String> ll1Array;

    private Character beginChar;

    //  终结符集
    private HashSet<Character> vtSet;
    //  非终结符集
    private HashSet<Character> vnSet;
    //  表达式
    private HashMap<Character, ArrayList<String>> expMap;

    private HashMap<Character, HashMap<String, HashSet<Character>>> selMap;

    private HashMap<Character, HashSet<Character>> firstMap;

    private HashMap<Character, HashSet<Character>> followMap;

    /**
     * 进行分析表构造器的初始化过程
     * @param ll1Array      表达式
     * @param beginChar     起始字符
     */
    public Grammer(ArrayList<String> ll1Array, Character beginChar) {
        this.ll1Array = ll1Array;
        this.beginChar = beginChar;

        vtSet = new HashSet<>();
        vnSet = new HashSet<>();
        expMap = new HashMap<>();

        selMap = new HashMap<>();
        firstMap = new HashMap<>();
        followMap = new HashMap<>();

        initSet();
        initExpMap();
        initFirstMap();
        initFollowMap();
        initSelectMap();
    }

    /**
     * 初始化非终结符集和终结符集
     */
    public void initSet() {
        //  构造非终结符集 即表达式的 -> 左边
        for (String sItem : ll1Array) {
            String[] split = sItem.split("->");
            vnSet.add(split[0].charAt(0));
        }
        //  构造终结符集
        for (String sItem : ll1Array) {
            String[] split = sItem.split("->");
            String vtStr = split[1];
            //  表达式的 -> 右边不一定单为终结符
            for (int i = 0; i < vtStr.length(); i++) {
                char cItem = vtStr.charAt(i);
                //  过滤掉非终结符
                if (!vnSet.contains(cItem))
                    vtSet.add(cItem);
            }
        }
    }

    /**
     * 初始化表达式表单
     * 主要任务是：将将所有表达式从数组中存放到map结构中便于操作
     */
    public void initExpMap() {
        for (String sItem : ll1Array) {
            String[] split = sItem.split("->");
            char c = split[0].charAt(0);
            if (!expMap.containsKey(c)) {
                ArrayList<String> expArray = new ArrayList<>();
                expArray.add(split[1]);
                expMap.put(c, expArray);
            } else {
                ArrayList<String> expArray = expMap.get(c);
                expArray.add(split[1]);
                expMap.put(c, expArray);
            }
        }
    }

    /**
     * 初始first集
     */
    public void initFirstMap() {
        for (Character vn : vnSet) {
            ArrayList<String> expArray = expMap.get(vn);
            for (String exp : expArray) {
                boolean doBreak = false;
//                System.out.println("initFirstMap:\tnow express is " + exp);
                for (int i = 0; i < exp.length(); i++) {
                    char c = exp.charAt(i);
                    HashSet<Character> firstMapItem = firstMap.get(vn);
                    if (firstMapItem == null) {
                        firstMapItem = new HashSet<Character>();
                    }
                    //  提前结束处理
                    doBreak = putItemToFirstMap(firstMapItem, vn, c);
                    if (doBreak)
                        break;
                }
            }
        }
    }

    /**
     * 构造first集
     * @param itemSet       集中的项
     * @param vnChar        非终结字符集
     * @param nowChar       要处理的当前字符
     * @return              构造成功与否
     */
    private boolean putItemToFirstMap(HashSet<Character> itemSet, Character vnChar, char nowChar) {
//        System.out.println("this vnChar is " + vnChar + "\tnowChar is " + nowChar);
        if (nowChar == 'ε' || vtSet.contains(nowChar)) {
            itemSet.add(nowChar);
            firstMap.put(vnChar, itemSet);
            return true;
        } else if (vnSet.contains(nowChar)) {
            ArrayList<String> expArrayCItem = expMap.get(nowChar);
            for (int i = 0; i < expArrayCItem.size(); i++) {
                String s = expArrayCItem.get(i);
                char cTmp = s.charAt(0);
                putItemToFirstMap(itemSet, vnChar, cTmp);
            }
        }
        return true;
    }

    /**
     * 初始化我follow集
     */
    public void initFollowMap() {
        for (Character vnChar : vnSet) {
            HashSet<Character> tmpSet = new HashSet<>();
            followMap.put(vnChar, tmpSet);
        }
        for (Character vn : vnSet) {
            Set<Character> expMapKeySet = expMap.keySet();
            for (Character expMapKey : expMapKeySet) {
                ArrayList<String> expMapValue = expMap.get(expMapKey);
                for (String exp : expMapValue) {
//                    System.out.println(expMapKey + "->" + exp);
                    HashSet<Character> followMapValue = followMap.get(vn);
                    putItemToFollow(vn, vn, expMapKey, exp, followMapValue);
                }
            }
        }
    }

    /**
     * 构造follow集
     * @param nowChar       当前处理的字符
     * @param cItem
     * @param keyChar
     * @param strItem
     * @param itemSet
     */
    private void putItemToFollow(Character nowChar, Character cItem,
                                 Character keyChar, String strItem, HashSet<Character> itemSet) {
        if (cItem.equals(beginChar)) {
            itemSet.add('#');
            followMap.put(nowChar, itemSet);
        }
        if (isContainAb(vtSet, strItem, cItem)) {
            Character lastChar = getLastChar(strItem, cItem);
            itemSet.add(lastChar);
            followMap.put(nowChar, itemSet);
        }
        if (isContainAB(vnSet, strItem, cItem)) {
            Character lastCharAB = getLastChar(strItem, cItem);
            HashSet<Character> firstMapSet = firstMap.get(lastCharAB);
            itemSet.addAll(firstMapSet);
            if (firstMapSet.contains('ε'))
                itemSet.add('#');
            itemSet.remove('ε');
            followMap.put(nowChar, itemSet);

            if (whetherAbIsNull(vnSet, strItem, cItem, expMap)) {
                if (!keyChar.equals(cItem)) {
                    Set<Character> keySet = expMap.keySet();
                    for (Character key : keySet) {
                        ArrayList<String> strArray = expMap.get(key);
                        for (String s : strArray) {
                            putItemToFollow(nowChar, keyChar, key, s, itemSet);
                        }
                    }
                }
            }
        }
        if (isContainbA(strItem, cItem)) {
            if (!keyChar.equals(cItem)) {
                Set<Character> keySet = expMap.keySet();
                for (Character key : keySet) {
                    ArrayList<String> strArray = expMap.get(key);
                    for (String s : strArray) {
                        putItemToFollow(nowChar, keyChar, key, s, itemSet);
                    }
                }
            }
        }
    }

    /**
     * 初始化 select 集
     */
    public void initSelectMap() {
        Set<Character> keySet = expMap.keySet();
        for (Character key : keySet) {
            HashMap<String, HashSet<Character>> selMapItem = new HashMap<>();
            ArrayList<String> expArray = expMap.get(key);
            for (String s : expArray) {
                char beginS = s.charAt(0);
                HashSet<Character> selectSet = new HashSet<>();
                if ('ε' == beginS) {
                    selectSet = followMap.get(key);
                    selectSet.remove('ε');
                    selMapItem.put(s, selectSet);
                }
                if (vtSet.contains(beginS)) {
                    selectSet.add(beginS);
                    selectSet.remove('ε');
                    selMapItem.put(s, selectSet);
                }
                if (vnSet.contains(beginS)) {
                    selectSet = firstMap.get(key);
                    selectSet.remove('ε');
                    selMapItem.put(s, selectSet);
                }
                selMap.put(key, selMapItem);
            }
        }
    }

    /**
     * 创建分析表
     * @return      分析表
     */
    public String[][] creatAlzTable() {
        Object[] vtArray = vtSet.toArray();
        Object[] vnArray = vnSet.toArray();

        this.analyzeTable = new String[vnArray.length + 1][vtArray.length + 1];

        System.out.println("------------------------------该算术表达式的LL(1)分析表为--------------------------------");
        System.out.print("表" + "\t\t");

        analyzeTable[0][0] = "Vn/Vt";
        for (int i = 0; i < vtArray.length; i++) {
            if (vtArray[i].equals('ε'))
                vtArray[i] = '#';
            System.out.print(vtArray[i] + "\t\t\t");
            analyzeTable[0][i + 1] = vtArray[i] + "";
        }
        System.out.println();
        for (int i = 0; i < vnArray.length; i++) {

            System.out.print(vnArray[i] + "\t\t");
            analyzeTable[i + 1][0] = vnArray[i] + "";
            for (int j = 0; j < vtArray.length; j++) {
                String exp = searchExp(selMap,
                        Character.valueOf((Character) vnArray[i]),
                        Character.valueOf((Character) vtArray[j]));
                if (exp == null) {
                    System.out.print("-\t\t\t");
                    analyzeTable[i + 1][j + 1] = "";
                } else {
                    System.out.print(vnArray[i] + "->" + exp + "\t\t");
//                    analyzeTable[i + 1][j + 1] = vnArray[i] + "->" + exp;
                    analyzeTable[i + 1][j + 1] = exp;
                }
            }
            System.out.println();
        }
        System.out.println("------------------------------------------------------------------------------------");
        return analyzeTable;
    }

    /**
     * 是否包含 Ab 型文法
     * @param vtSet
     * @param strItem
     * @param c
     * @return
     */
    private boolean isContainAb(HashSet<Character> vtSet, String strItem, Character c) {
        String s = c.toString();
        if (strItem.contains(s)) {
            int i = strItem.indexOf(s);
            String findStr;
            try {
                findStr = String.valueOf(strItem.charAt(i + 1));
            } catch (Exception e) {
                return false;
            }
            return vtSet.contains(findStr.charAt(0));
        } else
            return false;
    }

    private boolean whetherAbIsNull(HashSet<Character> vnSet, String strItem, Character c,
                                    HashMap<Character, ArrayList<String>> expMap) {
        String s = c.toString();
        if (isContainAB(vnSet, strItem, c)) {
            Character lastChar = getLastChar(strItem, c);
            ArrayList<String> expArray = expMap.get(lastChar);
            if (expArray.contains("ε")) {
//                System.out.println(lastChar + " : ('ε')" + s);
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含 AB 型文法
     * @param vnSet
     * @param strItem
     * @param c
     * @return
     */
    private boolean isContainAB(HashSet<Character> vnSet, String strItem, Character c) {
        String s = c.toString();
        if (strItem.contains(s)) {
            int i = strItem.indexOf(s);
            String findStr;
            try {
                findStr = String.valueOf(strItem.charAt(i + 1));
            } catch (Exception e) {
                return false;
            }
            return vnSet.contains(findStr.charAt(0));
        } else
            return false;
    }

    /**
     * 是否包含 bA 型文法
     * @param strItem
     * @param c
     * @return
     */
    private boolean isContainbA(String strItem, Character c) {
        String s = c.toString();
        String lastStr = strItem.substring(strItem.length() - 1);
        return lastStr.equals(s);
    }

    /**
     * 获取要处理的字符
     * @param strItem
     * @param c
     * @return
     */
    private Character getLastChar(String strItem, Character c) {
        String s = c.toString();
        if (strItem.contains(s)) {
            int i = strItem.indexOf(s);
            Character cTmp;
            try {
                cTmp = strItem.charAt(i + 1);
            } catch (Exception e) {
                return null;
            }
            return cTmp;
        }
        return null;
    }

    /**
     * 查找表达式
     * @param selectMap
     * @param keyChar
     * @param c
     * @return
     */
    private String searchExp(HashMap<Character, HashMap<String, HashSet<Character>>> selectMap,
                             Character keyChar, char c) {
        try {
            HashMap<String, HashSet<Character>> expStrMap = selectMap.get(keyChar);
            Set<String> keySet = expStrMap.keySet();
            for (String expSetKey : keySet) {
                HashSet<Character> expSet = expStrMap.get(expSetKey);
                if (expSet.contains(c))
                    return expSetKey;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
