package net.minecraft.advancement;

import java.util.Iterator;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.List;

public class AdvancementPositioner
{
    private final Advancement advancement;
    private final AdvancementPositioner parent;
    private final AdvancementPositioner previousSibling;
    private final int childrenSize;
    private final List<AdvancementPositioner> children;
    private AdvancementPositioner optionalLast;
    private AdvancementPositioner substituteChild;
    private int depth;
    private float row;
    private float relativeRowInSiblings;
    private float k;
    private float l;
    
    public AdvancementPositioner(final Advancement advancement, @Nullable final AdvancementPositioner advancementPositioner2, @Nullable final AdvancementPositioner advancementPositioner3, final int integer4, final int integer5) {
        this.children = Lists.newArrayList();
        if (advancement.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position an invisible advancement!");
        }
        this.advancement = advancement;
        this.parent = advancementPositioner2;
        this.previousSibling = advancementPositioner3;
        this.childrenSize = integer4;
        this.optionalLast = this;
        this.depth = integer5;
        this.row = -1.0f;
        AdvancementPositioner advancementPositioner4 = null;
        for (final Advancement advancement2 : advancement.getChildren()) {
            advancementPositioner4 = this.findChildrenRecursively(advancement2, advancementPositioner4);
        }
    }
    
    @Nullable
    private AdvancementPositioner findChildrenRecursively(final Advancement advancement, @Nullable AdvancementPositioner lastChild) {
        if (advancement.getDisplay() != null) {
            lastChild = new AdvancementPositioner(advancement, this, lastChild, this.children.size() + 1, this.depth + 1);
            this.children.add(lastChild);
        }
        else {
            for (final Advancement advancement2 : advancement.getChildren()) {
                lastChild = this.findChildrenRecursively(advancement2, lastChild);
            }
        }
        return lastChild;
    }
    
    private void calculateRecursively() {
        if (this.children.isEmpty()) {
            if (this.previousSibling != null) {
                this.row = this.previousSibling.row + 1.0f;
            }
            else {
                this.row = 0.0f;
            }
            return;
        }
        AdvancementPositioner advancementPositioner1 = null;
        for (final AdvancementPositioner advancementPositioner2 : this.children) {
            advancementPositioner2.calculateRecursively();
            advancementPositioner1 = advancementPositioner2.onFinishCalculation((advancementPositioner1 == null) ? advancementPositioner2 : advancementPositioner1);
        }
        this.onFinishChildrenCalculation();
        final float float2 = (this.children.get(0).row + this.children.get(this.children.size() - 1).row) / 2.0f;
        if (this.previousSibling != null) {
            this.row = this.previousSibling.row + 1.0f;
            this.relativeRowInSiblings = this.row - float2;
        }
        else {
            this.row = float2;
        }
    }
    
    private float findMinRowRecursively(final float deltaRow, final int depth, float minRow) {
        this.row += deltaRow;
        this.depth = depth;
        if (this.row < minRow) {
            minRow = this.row;
        }
        for (final AdvancementPositioner advancementPositioner5 : this.children) {
            minRow = advancementPositioner5.findMinRowRecursively(deltaRow + this.relativeRowInSiblings, depth + 1, minRow);
        }
        return minRow;
    }
    
    private void increaseRowRecursively(final float deltaRow) {
        this.row += deltaRow;
        for (final AdvancementPositioner advancementPositioner3 : this.children) {
            advancementPositioner3.increaseRowRecursively(deltaRow);
        }
    }
    
    private void onFinishChildrenCalculation() {
        float float1 = 0.0f;
        float float2 = 0.0f;
        for (int integer3 = this.children.size() - 1; integer3 >= 0; --integer3) {
            final AdvancementPositioner advancementPositioner5;
            final AdvancementPositioner advancementPositioner4 = advancementPositioner5 = this.children.get(integer3);
            advancementPositioner5.row += float1;
            final AdvancementPositioner advancementPositioner6 = advancementPositioner4;
            advancementPositioner6.relativeRowInSiblings += float1;
            float2 += advancementPositioner4.k;
            float1 += advancementPositioner4.l + float2;
        }
    }
    
    @Nullable
    private AdvancementPositioner getFirstChild() {
        if (this.substituteChild != null) {
            return this.substituteChild;
        }
        if (!this.children.isEmpty()) {
            return this.children.get(0);
        }
        return null;
    }
    
    @Nullable
    private AdvancementPositioner getLastChild() {
        if (this.substituteChild != null) {
            return this.substituteChild;
        }
        if (!this.children.isEmpty()) {
            return this.children.get(this.children.size() - 1);
        }
        return null;
    }
    
    private AdvancementPositioner onFinishCalculation(AdvancementPositioner last) {
        if (this.previousSibling == null) {
            return last;
        }
        AdvancementPositioner advancementPositioner2 = this;
        AdvancementPositioner advancementPositioner3 = this;
        AdvancementPositioner advancementPositioner4 = this.previousSibling;
        AdvancementPositioner advancementPositioner5 = this.parent.children.get(0);
        float float6 = this.relativeRowInSiblings;
        float float7 = this.relativeRowInSiblings;
        float float8 = advancementPositioner4.relativeRowInSiblings;
        float float9 = advancementPositioner5.relativeRowInSiblings;
        while (advancementPositioner4.getLastChild() != null && advancementPositioner2.getFirstChild() != null) {
            advancementPositioner4 = advancementPositioner4.getLastChild();
            advancementPositioner2 = advancementPositioner2.getFirstChild();
            advancementPositioner5 = advancementPositioner5.getFirstChild();
            advancementPositioner3 = advancementPositioner3.getLastChild();
            advancementPositioner3.optionalLast = this;
            final float float10 = advancementPositioner4.row + float8 - (advancementPositioner2.row + float6) + 1.0f;
            if (float10 > 0.0f) {
                advancementPositioner4.getLast(this, last).pushDown(this, float10);
                float6 += float10;
                float7 += float10;
            }
            float8 += advancementPositioner4.relativeRowInSiblings;
            float6 += advancementPositioner2.relativeRowInSiblings;
            float9 += advancementPositioner5.relativeRowInSiblings;
            float7 += advancementPositioner3.relativeRowInSiblings;
        }
        if (advancementPositioner4.getLastChild() != null && advancementPositioner3.getLastChild() == null) {
            advancementPositioner3.substituteChild = advancementPositioner4.getLastChild();
            final AdvancementPositioner advancementPositioner6 = advancementPositioner3;
            advancementPositioner6.relativeRowInSiblings += float8 - float7;
        }
        else {
            if (advancementPositioner2.getFirstChild() != null && advancementPositioner5.getFirstChild() == null) {
                advancementPositioner5.substituteChild = advancementPositioner2.getFirstChild();
                final AdvancementPositioner advancementPositioner7 = advancementPositioner5;
                advancementPositioner7.relativeRowInSiblings += float6 - float9;
            }
            last = this;
        }
        return last;
    }
    
    private void pushDown(final AdvancementPositioner advancementPositioner, final float extraRowDistance) {
        final float float3 = (float)(advancementPositioner.childrenSize - this.childrenSize);
        if (float3 != 0.0f) {
            advancementPositioner.k -= extraRowDistance / float3;
            this.k += extraRowDistance / float3;
        }
        advancementPositioner.l += extraRowDistance;
        advancementPositioner.row += extraRowDistance;
        advancementPositioner.relativeRowInSiblings += extraRowDistance;
    }
    
    private AdvancementPositioner getLast(final AdvancementPositioner advancementPositioner1, final AdvancementPositioner advancementPositioner2) {
        if (this.optionalLast != null && advancementPositioner1.parent.children.contains(this.optionalLast)) {
            return this.optionalLast;
        }
        return advancementPositioner2;
    }
    
    private void apply() {
        if (this.advancement.getDisplay() != null) {
            this.advancement.getDisplay().setPosition((float)this.depth, this.row);
        }
        if (!this.children.isEmpty()) {
            for (final AdvancementPositioner advancementPositioner2 : this.children) {
                advancementPositioner2.apply();
            }
        }
    }
    
    public static void arrangeForTree(final Advancement root) {
        if (root.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position children of an invisible root!");
        }
        final AdvancementPositioner advancementPositioner2 = new AdvancementPositioner(root, null, null, 1, 0);
        advancementPositioner2.calculateRecursively();
        final float float3 = advancementPositioner2.findMinRowRecursively(0.0f, 0, advancementPositioner2.row);
        if (float3 < 0.0f) {
            advancementPositioner2.increaseRowRecursively(-float3);
        }
        advancementPositioner2.apply();
    }
}
