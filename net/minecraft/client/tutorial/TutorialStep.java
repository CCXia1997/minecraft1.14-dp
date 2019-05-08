package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum TutorialStep
{
    MOVEMENT("movement", (Function<TutorialManager, T>)MovementTutorialStepHandler::new), 
    FIND_TREE("find_tree", (Function<TutorialManager, T>)FindTreeTutorialStepHandler::new), 
    PUNCH_TREE("punch_tree", (Function<TutorialManager, T>)PunchTreeTutorialStepHandler::new), 
    OPEN_INVENTORY("open_inventory", (Function<TutorialManager, T>)OpenInventoryTutorialStepHandler::new), 
    CRAFT_PLANKS("craft_planks", (Function<TutorialManager, T>)CraftPlanksTutorialStepHandler::new), 
    NONE("none", (Function<TutorialManager, T>)NoneTutorialStepHandler::new);
    
    private final String name;
    private final Function<TutorialManager, ? extends TutorialStepHandler> handlerFactory;
    
    private <T extends TutorialStepHandler> TutorialStep(final String string1, final Function<TutorialManager, T> function) {
        this.name = string1;
        this.handlerFactory = function;
    }
    
    public TutorialStepHandler createHandler(final TutorialManager tutorialManager) {
        return (TutorialStepHandler)this.handlerFactory.apply(tutorialManager);
    }
    
    public String getName() {
        return this.name;
    }
    
    public static TutorialStep byName(final String name) {
        for (final TutorialStep tutorialStep5 : values()) {
            if (tutorialStep5.name.equals(name)) {
                return tutorialStep5;
            }
        }
        return TutorialStep.NONE;
    }
}
