package rama.ir.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemRarityApi {
    private final List<Consumer<ApplyRarityEvent>> rarityEventListeners = new ArrayList<>();

    public void onApplyRarityEvent(Consumer<ApplyRarityEvent> listener){
        rarityEventListeners.add(listener);
    }
}
