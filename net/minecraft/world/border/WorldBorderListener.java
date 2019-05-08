package net.minecraft.world.border;

public interface WorldBorderListener
{
    void onSizeChange(final WorldBorder arg1, final double arg2);
    
    void onInterpolateSize(final WorldBorder arg1, final double arg2, final double arg3, final long arg4);
    
    void onCenterChanged(final WorldBorder arg1, final double arg2, final double arg3);
    
    void onWarningTimeChanged(final WorldBorder arg1, final int arg2);
    
    void onWarningBlocksChanged(final WorldBorder arg1, final int arg2);
    
    void onDamagePerBlockChanged(final WorldBorder arg1, final double arg2);
    
    void onSafeZoneChanged(final WorldBorder arg1, final double arg2);
    
    public static class WorldBorderSyncer implements WorldBorderListener
    {
        private final WorldBorder border;
        
        public WorldBorderSyncer(final WorldBorder border) {
            this.border = border;
        }
        
        @Override
        public void onSizeChange(final WorldBorder worldBorder, final double double2) {
            this.border.setSize(double2);
        }
        
        @Override
        public void onInterpolateSize(final WorldBorder border, final double fromSize, final double toSize, final long time) {
            this.border.interpolateSize(fromSize, toSize, time);
        }
        
        @Override
        public void onCenterChanged(final WorldBorder centerX, final double centerZ, final double double4) {
            this.border.setCenter(centerZ, double4);
        }
        
        @Override
        public void onWarningTimeChanged(final WorldBorder warningTime, final int integer) {
            this.border.setWarningTime(integer);
        }
        
        @Override
        public void onWarningBlocksChanged(final WorldBorder warningBlocks, final int integer) {
            this.border.setWarningBlocks(integer);
        }
        
        @Override
        public void onDamagePerBlockChanged(final WorldBorder damagePerBlock, final double double2) {
            this.border.setDamagePerBlock(double2);
        }
        
        @Override
        public void onSafeZoneChanged(final WorldBorder safeZoneRadius, final double double2) {
            this.border.setBuffer(double2);
        }
    }
}
