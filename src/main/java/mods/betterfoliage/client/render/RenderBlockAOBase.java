package mods.betterfoliage.client.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mods.betterfoliage.common.util.Double3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Block renderer base class. Stores calculated ambient occlusion light and color values when rendering
 *  block sides for later use.
 * @author octarine-noise
 */
@SideOnly(Side.CLIENT)
public class RenderBlockAOBase extends RenderBlocks {

	/** AO light and color values
	 * @author octarine-noise
	 */
	@SideOnly(Side.CLIENT)
	public static class ShadingValues {
		public int passCounter = 0;
		public int brightness;
		public float red;
		public float green;
		public float blue;
		public void setGray(float value) {
			red = value; green = value; blue = value;
		}
	}
	
	protected double[] uValues = new double[] {0.0, 16.0, 16.0, 0.0};
	protected double[] vValues = new double[] {0.0, 0.0, 16.0, 16.0};
	
	protected ForgeDirection[] faceDir1 = new ForgeDirection[] {ForgeDirection.WEST, ForgeDirection.WEST, ForgeDirection.WEST, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.NORTH};
	protected ForgeDirection[] faceDir2 = new ForgeDirection[] {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.UP, ForgeDirection.UP, ForgeDirection.UP, ForgeDirection.UP};
	
	/** Random vector pool. Unit rotation vectors in the XZ plane, Y coord goes between [-1.0, 1.0].
	 * Filled at init time */
	public Double3[] pRot = new Double3[64];
	
	/** Pool of random double values. Filled at init time. */
	public double[] pRand = new double[64];
	
	public ShadingValues aoXPYZPP = new ShadingValues();
	public ShadingValues aoXPYZPN = new ShadingValues();
	public ShadingValues aoXPYZNP = new ShadingValues();
	public ShadingValues aoXPYZNN = new ShadingValues();
	public ShadingValues aoXNYZPP = new ShadingValues();
	public ShadingValues aoXNYZPN = new ShadingValues();
	public ShadingValues aoXNYZNP = new ShadingValues();
	public ShadingValues aoXNYZNN = new ShadingValues();
	public ShadingValues aoYPXZPP = new ShadingValues();
	public ShadingValues aoYPXZPN = new ShadingValues();
	public ShadingValues aoYPXZNP = new ShadingValues();
	public ShadingValues aoYPXZNN = new ShadingValues();
	public ShadingValues aoYNXZPP = new ShadingValues();
	public ShadingValues aoYNXZPN = new ShadingValues();
	public ShadingValues aoYNXZNP = new ShadingValues();
	public ShadingValues aoYNXZNN = new ShadingValues();
	public ShadingValues aoZPXYPP = new ShadingValues();
	public ShadingValues aoZPXYPN = new ShadingValues();
	public ShadingValues aoZPXYNP = new ShadingValues();
	public ShadingValues aoZPXYNN = new ShadingValues();
	public ShadingValues aoZNXYPP = new ShadingValues();
	public ShadingValues aoZNXYPN = new ShadingValues();
	public ShadingValues aoZNXYNP = new ShadingValues();
	public ShadingValues aoZNXYNN = new ShadingValues();

	// temporary shading values for a single face
	public ShadingValues faceAOPP, faceAOPN, faceAONN, faceAONP;
	
	/** Initialize random values */
	public void init() {
		List<Double3> perturbs = new ArrayList<Double3>(64);
		for (int idx = 0; idx < 64; idx++) {
			double angle = (double) idx * Math.PI * 2.0 / 64.0;
			perturbs.add(new Double3(Math.cos(angle), Math.random() * 2.0 - 1.0, Math.sin(angle)));
			pRand[idx] = Math.random();
		}
		Collections.shuffle(perturbs);
		Iterator<Double3> iter = perturbs.iterator();
		for (int idx = 0; idx < 64; idx++) pRot[idx] = iter.next();
	}
			
	/** Get a semi-random value depending on block position.
	 * @param x block X coord
	 * @param y block Y coord
	 * @param z block Z coord
	 * @param seed additional seed
	 * @return semirandom value
	 */
	protected int getSemiRandomFromPos(double x, double y, double z, int seed) {
		long lx = MathHelper.floor_double(x);
		long ly = MathHelper.floor_double(y);
		long lz = MathHelper.floor_double(z);
		long value = (lx * lx + ly * ly + lz * lz + lx * ly + ly * lz + lz * lx + seed * seed) & 63;
		value = (3 * lx * value + 5 * ly * value + 7 * lz * value + 11 * seed) & 63;
		return (int) value;
	}
	
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		renderStandardBlockAsItem(renderer, block, metadata, 1.0f);
	}
	
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}
	
	public int getRenderId() {
		return 0;
	}
	
	protected void renderStandardBlockAsItem(RenderBlocks renderer, Block p_147800_1_, int p_147800_2_, float p_147800_3_) {
		Tessellator tessellator = Tessellator.instance;
		boolean flag = p_147800_1_ == Blocks.grass;
		
        float f2;
        float f3;
        int k;
        
		p_147800_1_.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(p_147800_1_);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(p_147800_1_, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(p_147800_1_, 0, p_147800_2_));
        tessellator.draw();

        if (flag && renderer.useInventoryTint)
        {
            k = p_147800_1_.getRenderColor(p_147800_2_);
            f2 = (float)(k >> 16 & 255) / 255.0F;
            f3 = (float)(k >> 8 & 255) / 255.0F;
            float f4 = (float)(k & 255) / 255.0F;
            GL11.glColor4f(f2 * p_147800_3_, f3 * p_147800_3_, f4 * p_147800_3_, 1.0F);
        }

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(p_147800_1_, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(p_147800_1_, 1, p_147800_2_));
        tessellator.draw();

        if (flag && renderer.useInventoryTint)
        {
            GL11.glColor4f(p_147800_3_, p_147800_3_, p_147800_3_, 1.0F);
        }

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(p_147800_1_, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(p_147800_1_, 2, p_147800_2_));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(p_147800_1_, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(p_147800_1_, 3, p_147800_2_));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(p_147800_1_, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(p_147800_1_, 4, p_147800_2_));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(p_147800_1_, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(p_147800_1_, 5, p_147800_2_));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	protected void setShadingForFace(ForgeDirection dir) {
		if (dir == ForgeDirection.DOWN) {
			// dir1 WEST, dir2 NORTH
			faceAOPP = aoYNXZPP; faceAOPN = aoYNXZPN; faceAONN = aoYNXZNN; faceAONP = aoYNXZNP;
		} else if (dir == ForgeDirection.UP) {
			// dir1 WEST, dir2 SOUTH
			faceAOPP = aoYPXZPP; faceAOPN = aoYPXZPN; faceAONN = aoYPXZNN; faceAONP = aoYPXZNP;
		} else if (dir == ForgeDirection.NORTH) {
			// dir1 WEST, dir2 UP
			faceAOPP = aoZNXYNP; faceAOPN = aoZNXYNN; faceAONN = aoZNXYPN; faceAONP = aoZNXYPP;
		} else if (dir == ForgeDirection.SOUTH) {
			// dir1 EAST, dir2 UP
			faceAOPP = aoZPXYPP; faceAOPN = aoZPXYPN; faceAONN = aoZPXYNN; faceAONP = aoZPXYNP;
		} else if (dir == ForgeDirection.WEST) {
			// dir1 SOUTH, dir2 UP
			faceAOPP = aoXNYZPP; faceAOPN = aoXNYZNP; faceAONN = aoXNYZNN; faceAONP = aoXNYZPN;
		} else if (dir == ForgeDirection.EAST) {
			// dir1 NORTH, dir2 UP
			faceAOPP = aoXPYZPN; faceAOPN = aoXPYZNN; faceAONN = aoXPYZNP; faceAONP = aoXPYZPP;
		}
	}
	
	public void renderCrossedSideQuads(Double3 drawBase, ForgeDirection dir, double scale, double halfHeight, Double3 rendomVec, double offset, IIcon renderIcon, int uvRot, boolean noShading) {
		Double3 facePP, faceNP, faceNormal, drawCenter;
		
		if (dir == ForgeDirection.UP) {
			// special case for block top, we'll be rendering a LOT of those
			facePP = new Double3(-scale, 0.0, scale);
			faceNP = new Double3(scale, 0.0, scale);
			faceNormal = new Double3(0.0, halfHeight, 0.0);
			drawCenter = drawBase.add(faceNormal);
			if (rendomVec != null) {
				drawCenter = drawBase.add(faceNormal).add(rendomVec.scaleAxes(-offset, 0.0, offset));
			}
		} else {
			facePP = new Double3(faceDir1[dir.ordinal()]).add(new Double3(faceDir2[dir.ordinal()])).scale(scale);
			faceNP = new Double3(faceDir1[dir.ordinal()]).inverse().add(new Double3(faceDir2[dir.ordinal()])).scale(scale);
			faceNormal = new Double3(dir).scale(halfHeight);
			drawCenter = drawBase.add(faceNormal);
			if (rendomVec != null) {
				drawCenter = drawCenter.add(new Double3(faceDir1[dir.ordinal()]).scale(rendomVec.x).scale(offset))
									   .add(new Double3(faceDir2[dir.ordinal()]).scale(rendomVec.z).scale(offset));
			}
		}
		
		if (Minecraft.isAmbientOcclusionEnabled() && !noShading) {
			setShadingForFace(dir);
			renderQuadWithShading(renderIcon, drawCenter, facePP, faceNormal, uvRot,			faceAOPP, faceAONN, faceAONN, faceAOPP);
			renderQuadWithShading(renderIcon, drawCenter, facePP.inverse(), faceNormal, uvRot,	faceAONN, faceAOPP, faceAOPP, faceAONN);
			renderQuadWithShading(renderIcon, drawCenter, faceNP, faceNormal, uvRot, 			faceAONP, faceAOPN, faceAOPN, faceAONP);
			renderQuadWithShading(renderIcon, drawCenter, faceNP.inverse(), faceNormal, uvRot,	faceAOPN, faceAONP, faceAONP, faceAOPN);
		} else {
			renderQuad(renderIcon, drawCenter, facePP, faceNormal, uvRot);
			renderQuad(renderIcon, drawCenter, facePP.inverse(), faceNormal, uvRot);
			renderQuad(renderIcon, drawCenter, faceNP, faceNormal, uvRot);
			renderQuad(renderIcon, drawCenter, faceNP.inverse(), faceNormal, uvRot);
		}
	}
	
	protected void renderCrossedBlockQuadsTranslate(Double3 blockCenter, double halfSize, Double3 offsetVec, IIcon crossLeafIcon, int uvRot, boolean isAirTop, boolean isAirBottom) {
		Double3 drawCenter = blockCenter;
		if (offsetVec != null) drawCenter = drawCenter.add(offsetVec);
		Double3 horz1 = new Double3(halfSize, 0.0, halfSize);
		Double3 horz2 = new Double3(halfSize, 0.0, -halfSize);
		Double3 vert1 = new Double3(0.0, halfSize * 1.41, 0.0);
		
		renderCrossedBlockQuadsInternal(drawCenter, horz1, horz2, vert1, crossLeafIcon, uvRot, isAirTop, isAirBottom);
	}
	
	protected void renderCrossedBlockQuadsSkew(Double3 blockCenter, double halfSize, Double3 offsetVec1, Double3 offsetVec2, IIcon crossLeafIcon, int uvRot, boolean isAirTop, boolean isAirBottom) {
		Double3 horz1 = new Double3(halfSize, 0.0, halfSize).add(offsetVec1);
		Double3 horz2 = new Double3(halfSize, 0.0, -halfSize).add(offsetVec2);
		Double3 vert1 = new Double3(0.0, halfSize * 1.41, 0.0);
		
		renderCrossedBlockQuadsInternal(blockCenter, horz1, horz2, vert1, crossLeafIcon, uvRot, isAirTop, isAirBottom);
	}
	
	private void renderCrossedBlockQuadsInternal(Double3 drawCenter, Double3 horz1, Double3 horz2, Double3 vert1, IIcon crossLeafIcon, int uvRot, boolean isAirTop, boolean isAirBottom) {
		if (Minecraft.isAmbientOcclusionEnabled()) {
			renderQuadWithShading(crossLeafIcon, drawCenter, horz1, vert1, uvRot,
					isAirTop ? aoYPXZPP : aoZPXYPP, isAirTop ? aoYPXZNN : aoXNYZPN, isAirBottom ? aoYNXZNN : aoXNYZNN, isAirBottom ? aoYNXZPP : aoZPXYPN);
			renderQuadWithShading(crossLeafIcon, drawCenter, horz1.inverse(), vert1, uvRot,
					isAirTop ? aoYPXZNN : aoZNXYNP, isAirTop ? aoYPXZPP : aoXPYZPP, isAirBottom ? aoYNXZPP : aoXPYZNP, isAirBottom ? aoYNXZNN : aoZNXYNN);
			renderQuadWithShading(crossLeafIcon, drawCenter, horz2, vert1, uvRot,
					isAirTop ? aoYPXZPN : aoXPYZPN, isAirTop ? aoYPXZNP : aoZPXYNP, isAirBottom ? aoYNXZNP : aoZPXYNN, isAirBottom ? aoYNXZPN : aoXPYZNN);
			renderQuadWithShading(crossLeafIcon, drawCenter, horz2.inverse(), vert1, uvRot,
					isAirTop ? aoYPXZNP : aoXNYZPP, isAirTop ? aoYPXZPN : aoZNXYPP, isAirBottom ? aoYNXZPN : aoZNXYPN, isAirBottom ? aoYNXZNP : aoXNYZNP);
		} else {
			renderQuad(crossLeafIcon, drawCenter, horz1, vert1, uvRot);
			renderQuad(crossLeafIcon, drawCenter, horz1.inverse(), vert1, uvRot);
			renderQuad(crossLeafIcon, drawCenter, horz2, vert1, uvRot);
			renderQuad(crossLeafIcon, drawCenter, horz2.inverse(), vert1, uvRot);
		}
	}
	
	@Override
	public void renderFaceZNeg(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceZNeg(block, x, y, z, icon);
		saveShadingTopLeft(aoZNXYPP);
		saveShadingTopRight(aoZNXYNP);
		saveShadingBottomLeft(aoZNXYPN);
		saveShadingBottomRight(aoZNXYNN);
	}

	@Override
	public void renderFaceZPos(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceZPos(block, x, y, z, icon);
		saveShadingTopLeft(aoZPXYNP);
		saveShadingTopRight(aoZPXYPP);
		saveShadingBottomLeft(aoZPXYNN);
		saveShadingBottomRight(aoZPXYPN);
	}

	@Override
	public void renderFaceXNeg(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceXNeg(block, x, y, z, icon);
		saveShadingTopLeft(aoXNYZPN);
		saveShadingTopRight(aoXNYZPP);
		saveShadingBottomLeft(aoXNYZNN);
		saveShadingBottomRight(aoXNYZNP);
	}

	@Override
	public void renderFaceXPos(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceXPos(block, x, y, z, icon);
		saveShadingTopLeft(aoXPYZPP);
		saveShadingTopRight(aoXPYZPN);
		saveShadingBottomLeft(aoXPYZNP);
		saveShadingBottomRight(aoXPYZNN);
	}

	@Override
	public void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceYNeg(block, x, y, z, icon);
		saveShadingTopLeft(aoYNXZNP);
		saveShadingTopRight(aoYNXZPP);
		saveShadingBottomLeft(aoYNXZNN);
		saveShadingBottomRight(aoYNXZPN);
	}

	@Override
	public void renderFaceYPos(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceYPos(block, x, y, z, icon);
		saveShadingTopLeft(aoYPXZPP);
		saveShadingTopRight(aoYPXZNP);
		saveShadingBottomLeft(aoYPXZPN);
		saveShadingBottomRight(aoYPXZNN);
	}
	
	protected void saveShadingTopLeft(ShadingValues values) {
		if (--values.passCounter != 0) return;
		values.brightness = brightnessTopLeft;
		values.red = colorRedTopLeft;
		values.green = colorGreenTopLeft;
		values.blue = colorBlueTopLeft;
	}
	
	protected void saveShadingTopRight(ShadingValues values) {
		if (--values.passCounter != 0) return;
		values.brightness = brightnessTopRight;
		values.red = colorRedTopRight;
		values.green = colorGreenTopRight;
		values.blue = colorBlueTopRight;
	}
	
	protected void saveShadingBottomLeft(ShadingValues values) {
		if (--values.passCounter != 0) return;
		values.brightness = brightnessBottomLeft;
		values.red = colorRedBottomLeft;
		values.green = colorGreenBottomLeft;
		values.blue = colorBlueBottomLeft;
	}
	
	protected void saveShadingBottomRight(ShadingValues values) {
		if (--values.passCounter != 0) return;
		values.brightness = brightnessBottomRight;
		values.red = colorRedBottomRight;
		values.green = colorGreenBottomRight;
		values.blue = colorBlueBottomRight;
	}
	
	/** Set pass counter on all shading value objects.
	 *  Used to collect AO values from a specific draw pass
	 *  if the underlying renderer draws overlays 
	 * @param value pass counter
	 */
	protected void setPassCounters(int value) {
		aoXPYZPP.passCounter = value;
		aoXPYZPN.passCounter = value;
		aoXPYZNP.passCounter = value;
		aoXPYZNN.passCounter = value;
		aoXNYZPP.passCounter = value;
		aoXNYZPN.passCounter = value;
		aoXNYZNP.passCounter = value;
		aoXNYZNN.passCounter = value;
		aoYPXZPP.passCounter = value;
		aoYPXZPN.passCounter = value;
		aoYPXZNP.passCounter = value;
		aoYPXZNN.passCounter = value;
		aoYNXZPP.passCounter = value;
		aoYNXZPN.passCounter = value;
		aoYNXZNP.passCounter = value;
		aoYNXZNN.passCounter = value;
		aoZPXYPP.passCounter = value;
		aoZPXYPN.passCounter = value;
		aoZPXYNP.passCounter = value;
		aoZPXYNN.passCounter = value;
		aoZNXYPP.passCounter = value;
		aoZNXYPN.passCounter = value;
		aoZNXYNP.passCounter = value;
		aoZNXYNN.passCounter = value;
	}
	
	/** Render textured quad
	 * @param icon texture to use
	 * @param center center of quad
	 * @param vec1 vector to the half-point of one of the sides
	 * @param vec2 vector to half-point of side next to vec1
	 * @param uvRot number of increments to rotate UV coordinates by
	 */
	protected void renderQuad(IIcon icon, Double3 center, Double3 vec1, Double3 vec2, int uvRot) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.addVertexWithUV(center.x + vec1.x + vec2.x, center.y + vec1.y + vec2.y, center.z + vec1.z + vec2.z, icon.getInterpolatedU(uValues[uvRot & 3]), icon.getInterpolatedV(vValues[uvRot & 3]));
		tessellator.addVertexWithUV(center.x - vec1.x + vec2.x, center.y - vec1.y + vec2.y, center.z - vec1.z + vec2.z, icon.getInterpolatedU(uValues[(uvRot + 1) & 3]), icon.getInterpolatedV(vValues[(uvRot + 1) & 3]));
		tessellator.addVertexWithUV(center.x - vec1.x - vec2.x, center.y - vec1.y - vec2.y, center.z - vec1.z - vec2.z, icon.getInterpolatedU(uValues[(uvRot + 2) & 3]), icon.getInterpolatedV(vValues[(uvRot + 2) & 3]));
		tessellator.addVertexWithUV(center.x + vec1.x - vec2.x, center.y + vec1.y - vec2.y, center.z + vec1.z - vec2.z, icon.getInterpolatedU(uValues[(uvRot + 3) & 3]), icon.getInterpolatedV(vValues[(uvRot + 3) & 3]));
	}
	
	/** Render textured quad using AO information
	 * @param icon texture to use
	 * @param center center of quad
	 * @param vec1 vector to the half-point of one of the sides
	 * @param vec2 vector to half-point of side next to vec1
	 * @param uvRot number of increments to rotate UV coordinates by
	 * @param aoPP AO values for vertex at (+vec1, +vec2)
	 * @param aoNP AO values for vertex at (-vec1, +vec2)
	 * @param aoNN AO values for vertex at (-vec1, -vec2)
	 * @param aoPN AO values for vertex at (+vec1, -vec2) 
	 */
	protected void renderQuadWithShading(IIcon icon, Double3 center, Double3 vec1, Double3 vec2, int uvRot, ShadingValues aoPP, ShadingValues aoNP, ShadingValues aoNN, ShadingValues aoPN) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(aoPP.brightness);
		tessellator.setColorOpaque_F(aoPP.red, aoPP.green, aoPP.blue);
		tessellator.addVertexWithUV(center.x + vec1.x + vec2.x, center.y + vec1.y + vec2.y, center.z + vec1.z + vec2.z, icon.getInterpolatedU(uValues[uvRot & 3]), icon.getInterpolatedV(vValues[uvRot & 3]));
		tessellator.setBrightness(aoNP.brightness);
		tessellator.setColorOpaque_F(aoNP.red, aoNP.green, aoNP.blue);
		tessellator.addVertexWithUV(center.x - vec1.x + vec2.x, center.y - vec1.y + vec2.y, center.z - vec1.z + vec2.z, icon.getInterpolatedU(uValues[(uvRot + 1) & 3]), icon.getInterpolatedV(vValues[(uvRot + 1) & 3]));
		tessellator.setBrightness(aoNN.brightness);
		tessellator.setColorOpaque_F(aoNN.red, aoNN.green, aoNN.blue);
		tessellator.addVertexWithUV(center.x - vec1.x - vec2.x, center.y - vec1.y - vec2.y, center.z - vec1.z - vec2.z, icon.getInterpolatedU(uValues[(uvRot + 2) & 3]), icon.getInterpolatedV(vValues[(uvRot + 2) & 3]));
		tessellator.setBrightness(aoPN.brightness);
		tessellator.setColorOpaque_F(aoPN.red, aoPN.green, aoPN.blue);
		tessellator.addVertexWithUV(center.x + vec1.x - vec2.x, center.y + vec1.y - vec2.y, center.z + vec1.z - vec2.z, icon.getInterpolatedU(uValues[(uvRot + 3) & 3]), icon.getInterpolatedV(vValues[(uvRot + 3) & 3]));
	}
	
	protected int getBrightness(Block block, int x, int y, int z) {
		return block.getMixedBrightnessForBlock(blockAccess, x, y, z);
	}
}
