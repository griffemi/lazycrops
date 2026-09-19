package xyz.funky493.lazycrops.machines;

import net.minecraft.block.entity.BlockEntity;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/**
 * Energy buffer that marks its owning block entity dirty whenever a transfer commits, so
 * power pushed in by a cable survives a chunk unload.
 */
public class MachineEnergyStorage extends SimpleEnergyStorage {

    private final BlockEntity owner;

    public MachineEnergyStorage(BlockEntity owner, long capacity, long maxInsert, long maxExtract) {
        super(capacity, maxInsert, maxExtract);
        this.owner = owner;
    }

    @Override
    protected void onFinalCommit() {
        owner.markDirty();
    }

    /**
     * Spends energy outside a transaction, for use from the server tick.
     * <p>
     * The machines always check affordability before doing any work, so there is nothing to
     * roll back and a full {@code Transaction} would only add overhead.
     *
     * @return true if the full amount was available and has been deducted
     */
    public boolean spend(long cost) {
        if (this.amount < cost) {
            return false;
        }
        this.amount -= cost;
        owner.markDirty();
        return true;
    }
}
