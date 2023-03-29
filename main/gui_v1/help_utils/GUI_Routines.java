package gui_v1.help_utils;

import java.util.LinkedList;

public interface GUI_Routines {
    final boolean TRUE_OPT = true;
    default void setAllFalse(boolean[] bArr){
        setAllTo(bArr, !TRUE_OPT);
    } default void setAllFalse(Boolean[] bArr){
        setAllTo(bArr, !TRUE_OPT);
    }
    default void setAllTo(Boolean[] bArr,boolean setTo ){
        for(int i=0; i < bArr.length; i++){
            if(setTo==TRUE_OPT){
                bArr[i]=true;
            }else {
                bArr[i]=false;
            }
        }
    }
    default void setAllTrue(boolean[] bArr){
        setAllTo(bArr, TRUE_OPT);
    }
    default void setAllTo(boolean[] bArr,boolean setTo ){
        for(int i=0; i < bArr.length; i++){
            bArr[i]=setTo;
        }
    }
    default void o(Object[] ob){
        for(Object o: ob){
            o(o+" ");
        }
    }
    default void o(Object ob){
        System.out.println(ob+"");
    }
    default void oLn(Object[] ob){
        for(Object o: ob){
            oLn(o+" ");
        }
    }
    default void oLn(Object ob){
        System.out.print(ob+" ");
    }

    default void oLn(Object[] ob, Character separator){
        for(Object o: ob){
            oLn(o+" "+separator+" ");
        }
    }
    default void oLn(boolean[] ob, Character separator){
        for(Object o: ob){
            oLn(o+" "+separator+" ");
        }
    }
    default void o(boolean ob){
        System.out.print(ob+" ");

    }

    default String[] createStrArrOfSize(int size){
        String[] answr = new String[size];
        clearStrArr(answr);
        return answr;
    }
    default void clearStrArr(String[] sArr){
        for(int i=0; i< sArr.length; i++){
            sArr[i] = "";
        }
    }

    default LinkedList<Integer>  getIndexesOfFalse(boolean[] getIndexesOfFAlse){
        LinkedList<Integer> inexes = new LinkedList<>();
        for(int i = 0; i< getIndexesOfFAlse.length; i++){
            if(!getIndexesOfFAlse[i]){
                inexes.addLast(i);
            }
        }
        return  inexes;
    }

    default String  trimStr(String s){
        return s.trim();

    }
    default String[] trimAllStr(String[] sArr){
        if(sArr == null){
            return null;
        }
        String[]  clearArr = new String[sArr.length];
        for(int i=0; i< sArr.length; i++ ){
            clearArr[i]= trimStr(sArr[i])+"";
        }
        return  clearArr;
    }

    default boolean  isTextInList(String text, LinkedList<String> list) {
        if (list==null) return false;
        for(String existBank: list){
            if(text.compareToIgnoreCase(existBank)==0){
                return true;
            }
        }
        return false;
    }
    default String[] getAsStringArr(LinkedList<String> ll){
        String[] out = new String[ll.size()];
        for(int i=0; i< ll.size(); i++){
            out[i] = ll.get(i)+"";
        }
        return out;
    }
}




