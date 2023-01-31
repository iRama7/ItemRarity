package rama.ir.itemhandler;

public class Potion {

    String potion_effect_name;
    Boolean isExtended;
    Boolean isUpgraded;

    public Potion(){

    }

    public void setPotion_effect_name(String potion_effect_name){
        this.potion_effect_name = potion_effect_name;
    }

    public void setExtended(Boolean extended) {
        isExtended = extended;
    }

    public void setUpgraded(Boolean upgraded) {
        isUpgraded = upgraded;
    }

    public String getPotion_effect_name() {
        return potion_effect_name;
    }

    public String getExtended() {
        if(isExtended){
            return "true";
        }else{
            return "false";
        }
    }

    public String getUpgraded() {
        if(isUpgraded){
            return "true";
        }else{
            return "false";
        }
    }
}
