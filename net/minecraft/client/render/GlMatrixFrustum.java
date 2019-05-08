package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.GlAllocationUtils;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlMatrixFrustum extends Frustum
{
    private static final GlMatrixFrustum INSTANCE;
    private final FloatBuffer projectionMatrixBuffer;
    private final FloatBuffer modelviewMatrixBuffer;
    private final FloatBuffer h;
    
    public GlMatrixFrustum() {
        this.projectionMatrixBuffer = GlAllocationUtils.allocateFloatBuffer(16);
        this.modelviewMatrixBuffer = GlAllocationUtils.allocateFloatBuffer(16);
        this.h = GlAllocationUtils.allocateFloatBuffer(16);
    }
    
    public static Frustum get() {
        GlMatrixFrustum.INSTANCE.loadFromGlMatrices();
        return GlMatrixFrustum.INSTANCE;
    }
    
    private void normalize(final float[] vector) {
        final float float2 = MathHelper.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        final int n = 0;
        vector[n] /= float2;
        final int n2 = 1;
        vector[n2] /= float2;
        final int n3 = 2;
        vector[n3] /= float2;
        final int n4 = 3;
        vector[n4] /= float2;
    }
    
    public void loadFromGlMatrices() {
        this.projectionMatrixBuffer.clear();
        this.modelviewMatrixBuffer.clear();
        this.h.clear();
        GlStateManager.getMatrix(2983, this.projectionMatrixBuffer);
        GlStateManager.getMatrix(2982, this.modelviewMatrixBuffer);
        final float[] arr1 = this.projectionMatrix;
        final float[] arr2 = this.modelViewMatrix;
        this.projectionMatrixBuffer.flip().limit(16);
        this.projectionMatrixBuffer.get(arr1);
        this.modelviewMatrixBuffer.flip().limit(16);
        this.modelviewMatrixBuffer.get(arr2);
        this.mvpMatrix[0] = arr2[0] * arr1[0] + arr2[1] * arr1[4] + arr2[2] * arr1[8] + arr2[3] * arr1[12];
        this.mvpMatrix[1] = arr2[0] * arr1[1] + arr2[1] * arr1[5] + arr2[2] * arr1[9] + arr2[3] * arr1[13];
        this.mvpMatrix[2] = arr2[0] * arr1[2] + arr2[1] * arr1[6] + arr2[2] * arr1[10] + arr2[3] * arr1[14];
        this.mvpMatrix[3] = arr2[0] * arr1[3] + arr2[1] * arr1[7] + arr2[2] * arr1[11] + arr2[3] * arr1[15];
        this.mvpMatrix[4] = arr2[4] * arr1[0] + arr2[5] * arr1[4] + arr2[6] * arr1[8] + arr2[7] * arr1[12];
        this.mvpMatrix[5] = arr2[4] * arr1[1] + arr2[5] * arr1[5] + arr2[6] * arr1[9] + arr2[7] * arr1[13];
        this.mvpMatrix[6] = arr2[4] * arr1[2] + arr2[5] * arr1[6] + arr2[6] * arr1[10] + arr2[7] * arr1[14];
        this.mvpMatrix[7] = arr2[4] * arr1[3] + arr2[5] * arr1[7] + arr2[6] * arr1[11] + arr2[7] * arr1[15];
        this.mvpMatrix[8] = arr2[8] * arr1[0] + arr2[9] * arr1[4] + arr2[10] * arr1[8] + arr2[11] * arr1[12];
        this.mvpMatrix[9] = arr2[8] * arr1[1] + arr2[9] * arr1[5] + arr2[10] * arr1[9] + arr2[11] * arr1[13];
        this.mvpMatrix[10] = arr2[8] * arr1[2] + arr2[9] * arr1[6] + arr2[10] * arr1[10] + arr2[11] * arr1[14];
        this.mvpMatrix[11] = arr2[8] * arr1[3] + arr2[9] * arr1[7] + arr2[10] * arr1[11] + arr2[11] * arr1[15];
        this.mvpMatrix[12] = arr2[12] * arr1[0] + arr2[13] * arr1[4] + arr2[14] * arr1[8] + arr2[15] * arr1[12];
        this.mvpMatrix[13] = arr2[12] * arr1[1] + arr2[13] * arr1[5] + arr2[14] * arr1[9] + arr2[15] * arr1[13];
        this.mvpMatrix[14] = arr2[12] * arr1[2] + arr2[13] * arr1[6] + arr2[14] * arr1[10] + arr2[15] * arr1[14];
        this.mvpMatrix[15] = arr2[12] * arr1[3] + arr2[13] * arr1[7] + arr2[14] * arr1[11] + arr2[15] * arr1[15];
        final float[] arr3 = this.sides[0];
        arr3[0] = this.mvpMatrix[3] - this.mvpMatrix[0];
        arr3[1] = this.mvpMatrix[7] - this.mvpMatrix[4];
        arr3[2] = this.mvpMatrix[11] - this.mvpMatrix[8];
        arr3[3] = this.mvpMatrix[15] - this.mvpMatrix[12];
        this.normalize(arr3);
        final float[] arr4 = this.sides[1];
        arr4[0] = this.mvpMatrix[3] + this.mvpMatrix[0];
        arr4[1] = this.mvpMatrix[7] + this.mvpMatrix[4];
        arr4[2] = this.mvpMatrix[11] + this.mvpMatrix[8];
        arr4[3] = this.mvpMatrix[15] + this.mvpMatrix[12];
        this.normalize(arr4);
        final float[] arr5 = this.sides[2];
        arr5[0] = this.mvpMatrix[3] + this.mvpMatrix[1];
        arr5[1] = this.mvpMatrix[7] + this.mvpMatrix[5];
        arr5[2] = this.mvpMatrix[11] + this.mvpMatrix[9];
        arr5[3] = this.mvpMatrix[15] + this.mvpMatrix[13];
        this.normalize(arr5);
        final float[] arr6 = this.sides[3];
        arr6[0] = this.mvpMatrix[3] - this.mvpMatrix[1];
        arr6[1] = this.mvpMatrix[7] - this.mvpMatrix[5];
        arr6[2] = this.mvpMatrix[11] - this.mvpMatrix[9];
        arr6[3] = this.mvpMatrix[15] - this.mvpMatrix[13];
        this.normalize(arr6);
        final float[] arr7 = this.sides[4];
        arr7[0] = this.mvpMatrix[3] - this.mvpMatrix[2];
        arr7[1] = this.mvpMatrix[7] - this.mvpMatrix[6];
        arr7[2] = this.mvpMatrix[11] - this.mvpMatrix[10];
        arr7[3] = this.mvpMatrix[15] - this.mvpMatrix[14];
        this.normalize(arr7);
        final float[] arr8 = this.sides[5];
        arr8[0] = this.mvpMatrix[3] + this.mvpMatrix[2];
        arr8[1] = this.mvpMatrix[7] + this.mvpMatrix[6];
        arr8[2] = this.mvpMatrix[11] + this.mvpMatrix[10];
        arr8[3] = this.mvpMatrix[15] + this.mvpMatrix[14];
        this.normalize(arr8);
    }
    
    static {
        INSTANCE = new GlMatrixFrustum();
    }
}
