package net.minecraft.data.server;

import net.minecraft.tag.TagContainer;
import java.nio.file.Path;
import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.block.Block;

public class BlockTagsProvider extends AbstractTagProvider<Block>
{
    public BlockTagsProvider(final DataGenerator dataGenerator) {
        super(dataGenerator, Registry.BLOCK);
    }
    
    @Override
    protected void configure() {
        this.a(BlockTags.a).add(Blocks.aX, Blocks.aY, Blocks.aZ, Blocks.ba, Blocks.bb, Blocks.bc, Blocks.bd, Blocks.be, Blocks.bf, Blocks.bg, Blocks.bh, Blocks.bi, Blocks.bj, Blocks.bk, Blocks.bl, Blocks.bm);
        this.a(BlockTags.b).add(Blocks.n, Blocks.o, Blocks.p, Blocks.q, Blocks.r, Blocks.s);
        this.a(BlockTags.c).add(Blocks.dn, Blocks.do_, Blocks.dp, Blocks.dq);
        this.a(BlockTags.d).add(Blocks.eO, Blocks.eP, Blocks.eQ, Blocks.eR, Blocks.eS, Blocks.eT);
        this.a(BlockTags.e).add(BlockTags.d).add(Blocks.cz);
        this.a(BlockTags.f).add(Blocks.gt, Blocks.gu, Blocks.gv, Blocks.gw, Blocks.gx, Blocks.gy, Blocks.gz, Blocks.gA, Blocks.gB, Blocks.gC, Blocks.gD, Blocks.gE, Blocks.gF, Blocks.gG, Blocks.gH, Blocks.gI);
        this.a(BlockTags.g).add(Blocks.cd, Blocks.ij, Blocks.ik, Blocks.il, Blocks.im, Blocks.in);
        this.a(BlockTags.h).add(Blocks.bO, Blocks.eg, Blocks.eh, Blocks.ei, Blocks.gd, Blocks.ge);
        this.a(BlockTags.i).add(Blocks.hC, Blocks.hD, Blocks.hE, Blocks.hF, Blocks.hG, Blocks.hH);
        this.a(BlockTags.j).add(Blocks.cH, Blocks.ih, Blocks.ii, Blocks.ie, Blocks.if_, Blocks.ig);
        this.a(BlockTags.m).add(BlockTags.g).add(Blocks.cp);
        this.a(BlockTags.n).add(Blocks.t, Blocks.u, Blocks.v, Blocks.w, Blocks.x, Blocks.y);
        this.a(BlockTags.p).add(Blocks.N, Blocks.Z, Blocks.S, Blocks.af);
        this.a(BlockTags.q).add(Blocks.I, Blocks.U, Blocks.T, Blocks.aa);
        this.a(BlockTags.s).add(Blocks.M, Blocks.Y, Blocks.R, Blocks.ae);
        this.a(BlockTags.r).add(Blocks.K, Blocks.W, Blocks.P, Blocks.ac);
        this.a(BlockTags.t).add(Blocks.L, Blocks.X, Blocks.Q, Blocks.ad);
        this.a(BlockTags.u).add(Blocks.J, Blocks.V, Blocks.O, Blocks.ab);
        this.a(BlockTags.o).add(BlockTags.p).add(BlockTags.q).add(BlockTags.s).add(BlockTags.r).add(BlockTags.t).add(BlockTags.u);
        this.a(BlockTags.A).add(Blocks.fg, Blocks.fh, Blocks.fi);
        this.a(BlockTags.E).add(Blocks.bo, Blocks.bp, Blocks.bq, Blocks.br, Blocks.bs, Blocks.bt, Blocks.bu, Blocks.bv, Blocks.bw, Blocks.bx, Blocks.by, Blocks.bA, Blocks.bz);
        this.a(BlockTags.I).add(BlockTags.E).add(Blocks.i, Blocks.j, Blocks.k, Blocks.l, Blocks.C, Blocks.D, Blocks.E, Blocks.bB, Blocks.bC, Blocks.bG, Blocks.cD, Blocks.cE, Blocks.cI, Blocks.cN, Blocks.dC, Blocks.dL, Blocks.cJ);
        this.a(BlockTags.H).add(Blocks.en, Blocks.ew, Blocks.ex, Blocks.ey, Blocks.ez, Blocks.eA, Blocks.eB, Blocks.eC, Blocks.eD, Blocks.eE, Blocks.ev, Blocks.eo, Blocks.ep, Blocks.eq, Blocks.er, Blocks.es, Blocks.et, Blocks.eI, Blocks.eJ, Blocks.eK, Blocks.eu, Blocks.eL, Blocks.eF, Blocks.eG, Blocks.eH, Blocks.kR);
        this.a(BlockTags.v).add(Blocks.gS, Blocks.gT, Blocks.gU, Blocks.gV, Blocks.gW, Blocks.gX, Blocks.gY, Blocks.gZ, Blocks.ha, Blocks.hb, Blocks.hc, Blocks.hd, Blocks.he, Blocks.hf, Blocks.hg, Blocks.hh, Blocks.hi, Blocks.hj, Blocks.hk, Blocks.hl, Blocks.hm, Blocks.hn, Blocks.ho, Blocks.hp, Blocks.hq, Blocks.hr, Blocks.hs, Blocks.ht, Blocks.hu, Blocks.hv, Blocks.hw, Blocks.hx);
        this.a(BlockTags.k).add(Blocks.cq, Blocks.cr, Blocks.cs, Blocks.ct, Blocks.cu, Blocks.cv);
        this.a(BlockTags.x).add(Blocks.bO, Blocks.cg, Blocks.eg, Blocks.ea, Blocks.gd, Blocks.ei, Blocks.eh, Blocks.ge, Blocks.dP, Blocks.dK, Blocks.dJ, Blocks.it, Blocks.fu, Blocks.hB, Blocks.gm, Blocks.gl, Blocks.gn, Blocks.kV, Blocks.kW, Blocks.kX, Blocks.kY, Blocks.kZ, Blocks.la, Blocks.lb, Blocks.lc, Blocks.ld, Blocks.le, Blocks.lf, Blocks.lg, Blocks.lh, Blocks.li);
        this.a(BlockTags.y).add(Blocks.hI, Blocks.hJ, Blocks.hP, Blocks.hK, Blocks.hG, Blocks.hE, Blocks.hH, Blocks.hF, Blocks.hC, Blocks.hD, Blocks.hU, Blocks.hR, Blocks.hS, Blocks.hO, Blocks.hN, Blocks.hQ, Blocks.hM, Blocks.go, Blocks.gp, Blocks.gq, Blocks.lj, Blocks.lk, Blocks.ll, Blocks.lm, Blocks.ln, Blocks.lo, Blocks.lp, Blocks.lq, Blocks.lr, Blocks.ls, Blocks.lt, Blocks.lu, Blocks.lv);
        this.a(BlockTags.z).add(Blocks.el, Blocks.em, Blocks.lw, Blocks.lx, Blocks.ly, Blocks.lz, Blocks.lA, Blocks.lB, Blocks.lC, Blocks.lD, Blocks.lE, Blocks.lF, Blocks.lG, Blocks.lH);
        this.a(BlockTags.P).add(Blocks.kn, Blocks.ko, Blocks.kp, Blocks.kq, Blocks.kr);
        this.a(BlockTags.Q).add(BlockTags.P).add(Blocks.kx, Blocks.ky, Blocks.kz, Blocks.kA, Blocks.kB);
        this.a(BlockTags.O).add(Blocks.kH, Blocks.kI, Blocks.kJ, Blocks.kK, Blocks.kL);
        this.a(BlockTags.w).add(Blocks.C, Blocks.D);
        this.a(BlockTags.B).add(Blocks.cf, Blocks.aM, Blocks.aN, Blocks.fv);
        this.a(BlockTags.N).add(Blocks.kd, Blocks.ke, Blocks.kf, Blocks.kg, Blocks.kh);
        this.a(BlockTags.J).add(Blocks.cB, Blocks.gL, Blocks.kN, Blocks.iA);
        this.a(BlockTags.K).add(Blocks.i, Blocks.l);
        this.a(BlockTags.C).add(Blocks.aj, Blocks.ag, Blocks.ah, Blocks.al, Blocks.ak, Blocks.ai);
        this.a(BlockTags.L).add(Blocks.ao, Blocks.cR, Blocks.cS, Blocks.cT, Blocks.cU, Blocks.cV, Blocks.cW, Blocks.cX, Blocks.cY, Blocks.cZ, Blocks.da, Blocks.db, Blocks.dc, Blocks.dd, Blocks.de, Blocks.df, Blocks.dg);
        this.a(BlockTags.l).add(Blocks.dl, Blocks.dj, Blocks.dm, Blocks.dk, Blocks.dh, Blocks.di);
        this.a(BlockTags.D).add(BlockTags.l).add(Blocks.gh);
        this.a(BlockTags.M).add(Blocks.aT).add(BlockTags.Q).add(BlockTags.O);
        this.a(BlockTags.S).add(Blocks.j).add(Blocks.i).add(Blocks.l).add(Blocks.k).add(Blocks.dL);
        this.a(BlockTags.R).add(Blocks.kQ).add(Blocks.kP).add(Blocks.E).add(BlockTags.w).add(BlockTags.S);
        this.a(BlockTags.T).add(Blocks.bX, Blocks.bY, Blocks.bZ, Blocks.ca, Blocks.cb, Blocks.cc);
        this.a(BlockTags.U).add(Blocks.ch, Blocks.ci, Blocks.cj, Blocks.ck, Blocks.cl, Blocks.cm);
        this.a(BlockTags.V).add(BlockTags.T).add(BlockTags.U);
        this.a(BlockTags.F).add(Blocks.aK, Blocks.aL, Blocks.aH, Blocks.aI, Blocks.aF, Blocks.aD, Blocks.aJ, Blocks.az, Blocks.aE, Blocks.aB, Blocks.ay, Blocks.ax, Blocks.aC, Blocks.aG, Blocks.aw, Blocks.aA);
        this.a(BlockTags.G).add(BlockTags.j).add(Blocks.dO);
        this.a(BlockTags.W).add(Blocks.gg, Blocks.z, Blocks.dU, Blocks.dV, Blocks.ix, Blocks.ej, Blocks.iy, Blocks.iz, Blocks.lX, Blocks.lY, Blocks.bn, Blocks.bJ, Blocks.dW, Blocks.dA);
        this.a(BlockTags.X).add(Blocks.gg, Blocks.z, Blocks.dU, Blocks.dV, Blocks.ix, Blocks.ej, Blocks.iy, Blocks.iz, Blocks.lX, Blocks.lY, Blocks.bn);
    }
    
    @Override
    protected Path getOutput(final Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/blocks/" + identifier.getPath() + ".json");
    }
    
    @Override
    public String getName() {
        return "Block Tags";
    }
    
    @Override
    protected void a(final TagContainer<Block> tagContainer) {
        BlockTags.setContainer(tagContainer);
    }
}
