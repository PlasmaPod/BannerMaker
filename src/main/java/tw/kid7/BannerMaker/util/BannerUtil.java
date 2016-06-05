package tw.kid7.BannerMaker.util;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Dye;

import java.util.*;

public class BannerUtil {
    /**
     * 檢查ItemStack是否為旗幟
     *
     * @param itemStack 欲檢查的物品
     * @return boolean
     */
    static public boolean isBanner(ItemStack itemStack) {
        return itemStack != null && itemStack.getType().equals(Material.BANNER);
    }

    /**
     * 取得旗幟材料清單
     *
     * @param itemStack 欲取得材料清單之旗幟
     * @return List<ItemStack>
     */
    static public List<ItemStack> getMaterials(ItemStack itemStack) {
        List<ItemStack> materialList = new ArrayList<>();
        //只檢查旗幟
        if (!isBanner(itemStack)) {
            return materialList;
        }
        //基本材料
        //木棒
        ItemStack stick = new ItemStack(Material.STICK, 1);
        materialList.add(stick);
        //羊毛
        //顏色
        int color = 15 - itemStack.getDurability();
        //羊毛
        ItemStack wool = new ItemStack(Material.WOOL, 6, (short) color);
        materialList.add(wool);
        //Pattern材料
        Inventory materialInventory = Bukkit.createInventory(null, 54);
        BannerMeta bm = (BannerMeta) itemStack.getItemMeta();
        //逐Pattern計算
        for (Pattern pattern : bm.getPatterns()) {
            //所需染料
            Dye dye = new Dye();
            dye.setColor(pattern.getColor());
            switch (pattern.getPattern()) {
                case SQUARE_BOTTOM_LEFT:
                case SQUARE_BOTTOM_RIGHT:
                case SQUARE_TOP_LEFT:
                case SQUARE_TOP_RIGHT:
                case CIRCLE_MIDDLE:
                    materialInventory.addItem(dye.toItemStack(1));
                    break;
                case STRIPE_BOTTOM:
                case STRIPE_TOP:
                case STRIPE_LEFT:
                case STRIPE_RIGHT:
                case STRIPE_CENTER:
                case STRIPE_MIDDLE:
                case STRIPE_DOWNRIGHT:
                case STRIPE_DOWNLEFT:
                case TRIANGLE_BOTTOM:
                case TRIANGLE_TOP:
                case TRIANGLES_BOTTOM:
                case TRIANGLES_TOP:
                case DIAGONAL_LEFT:
                case DIAGONAL_RIGHT:
                case DIAGONAL_LEFT_MIRROR:
                case DIAGONAL_RIGHT_MIRROR:
                    materialInventory.addItem(dye.toItemStack(3));
                    break;
                case STRIPE_SMALL:
                case RHOMBUS_MIDDLE:
                case GRADIENT:
                case GRADIENT_UP:
                    materialInventory.addItem(dye.toItemStack(4));
                    break;
                case CROSS:
                case STRAIGHT_CROSS:
                    materialInventory.addItem(dye.toItemStack(5));
                    break;
                case HALF_VERTICAL:
                case HALF_HORIZONTAL:
                case HALF_VERTICAL_MIRROR:
                case HALF_HORIZONTAL_MIRROR:
                    materialInventory.addItem(dye.toItemStack(6));
                    break;
                case BORDER:
                    materialInventory.addItem(dye.toItemStack(8));
                    break;
                case CURLY_BORDER:
                    materialInventory.addItem(new ItemStack(Material.VINE));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case CREEPER:
                    materialInventory.addItem(new ItemStack(Material.SKULL_ITEM, 1, (short) 4));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case BRICKS:
                    materialInventory.addItem(new ItemStack(Material.BRICK));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case SKULL:
                    materialInventory.addItem(new ItemStack(Material.SKULL_ITEM, 1, (short) 1));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case FLOWER:
                    materialInventory.addItem(new ItemStack(Material.RED_ROSE, 1, (short) 8));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case MOJANG:
                    materialInventory.addItem(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
            }
        }
        //加到暫存清單
        List<ItemStack> patternMaterials = new ArrayList<>();
        Collections.addAll(patternMaterials, materialInventory.getContents());
        //移除空值
        patternMaterials.removeAll(Collections.singletonList(null));
        //重新排序
        Collections.sort(patternMaterials, new Comparator<ItemStack>() {
            public int compare(ItemStack itemStack1, ItemStack itemStack2) {
                if (itemStack1.getTypeId() != itemStack2.getTypeId()) {
                    return itemStack1.getTypeId() - itemStack2.getTypeId();
                }
                return itemStack1.getDurability() - itemStack2.getDurability();
            }
        });
        //將材料加到清單中
        materialList.addAll(patternMaterials);

        return materialList;
    }

    /**
     * 檢查是否擁有足夠材料
     *
     * @param inventory 指定物品欄
     * @param itemStack 旗幟
     * @return 是否擁有足夠材料
     */
    static public boolean hasEnoughMaterials(Inventory inventory, ItemStack itemStack) {
        //只檢查旗幟
        if (!isBanner(itemStack)) {
            return false;
        }
        //材料清單
        List<ItemStack> materials = getMaterials(itemStack);
        for (ItemStack material : materials) {
            //任何一項不足
            if (!inventory.containsAtLeast(material, material.getAmount())) {
                //直接回傳false
                return false;
            }
        }
        return true;
    }

    /**
     * 從物品欄移除材料
     *
     * @param inventory 指定物品欄
     * @param itemStack 旗幟
     * @return 是否順利移除材料
     */
    static public boolean removeMaterials(Inventory inventory, ItemStack itemStack) {
        //只檢查旗幟
        if (!isBanner(itemStack)) {
            return false;
        }
        //材料必須足夠
        if (!hasEnoughMaterials(inventory, itemStack)) {
            return false;
        }
        //材料清單
        List<ItemStack> materials = getMaterials(itemStack);
        HashMap<Integer, ItemStack> itemCannotRemoved = inventory.removeItem(materials.toArray(new ItemStack[materials.size()]));
        if (!itemCannotRemoved.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 取得旗幟在玩家存檔中的Key
     *
     * @param itemStack 欲檢查之旗幟
     * @return String
     */
    static public String getKey(ItemStack itemStack) {
        //只處理旗幟
        if (!isBanner(itemStack)) {
            return null;
        }
        String key;
        //嘗試取出key
        try {
            key = HiddenStringUtil.extractHiddenString(itemStack.getItemMeta().getLore().get(0));
        } catch (Exception exception) {
            return null;
        }
        return key;
    }

    /**
     * 取得旗幟名稱，若無名稱則嘗試取得KEY
     *
     * @param itemStack 欲檢查之旗幟
     * @return String
     */
    static public String getName(ItemStack itemStack) {
        //只處理旗幟
        if (!isBanner(itemStack)) {
            return null;
        }
        String showName = "";
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            showName = itemStack.getItemMeta().getDisplayName();
        } else {
            String key = BannerUtil.getKey(itemStack);
            if (key != null) {
                showName = key;
            }
        }
        return showName;
    }

    public static List<PatternType> getPatternTypeList() {
        List<PatternType> list = Arrays.asList(
            PatternType.BORDER,
            PatternType.BRICKS,
            PatternType.CIRCLE_MIDDLE,
            PatternType.CREEPER,
            PatternType.CROSS,
            PatternType.CURLY_BORDER,
            PatternType.DIAGONAL_LEFT,
            PatternType.DIAGONAL_LEFT_MIRROR,
            PatternType.DIAGONAL_RIGHT,
            PatternType.DIAGONAL_RIGHT_MIRROR,
            PatternType.FLOWER,
            PatternType.GRADIENT,
            PatternType.GRADIENT_UP,
            PatternType.HALF_HORIZONTAL,
            PatternType.HALF_HORIZONTAL_MIRROR,
            PatternType.HALF_VERTICAL,
            PatternType.HALF_VERTICAL_MIRROR,
            PatternType.MOJANG,
            PatternType.RHOMBUS_MIDDLE,
            PatternType.SKULL,
            PatternType.SQUARE_BOTTOM_LEFT,
            PatternType.SQUARE_BOTTOM_RIGHT,
            PatternType.SQUARE_TOP_LEFT,
            PatternType.SQUARE_TOP_RIGHT,
            PatternType.STRAIGHT_CROSS,
            PatternType.STRIPE_BOTTOM,
            PatternType.STRIPE_CENTER,
            PatternType.STRIPE_DOWNLEFT,
            PatternType.STRIPE_DOWNRIGHT,
            PatternType.STRIPE_LEFT,
            PatternType.STRIPE_MIDDLE,
            PatternType.STRIPE_RIGHT,
            PatternType.STRIPE_SMALL,
            PatternType.STRIPE_TOP,
            PatternType.TRIANGLE_BOTTOM,
            PatternType.TRIANGLE_TOP,
            PatternType.TRIANGLES_BOTTOM,
            PatternType.TRIANGLES_TOP
        );
        return list;
    }

    static public HashMap<Integer, ItemStack> getPatternRecipe(final ItemStack banner, int step) {
        HashMap<Integer, ItemStack> recipe = Maps.newHashMap();
        //填滿空氣
        for (int i = 0; i < 10; i++) {
            recipe.put(i, new ItemStack(Material.AIR));
        }
        //只處理旗幟
        if (banner != null && banner.getType().equals(Material.BANNER)) {
            BannerMeta bm = (BannerMeta) banner.getItemMeta();
            int totalStep = bm.numberOfPatterns() + 1;
            if (step == 1) {
                //第一步，旗幟合成
                //顏色
                int color = 15 - banner.getDurability();
                //羊毛
                ItemStack wool = new ItemStack(Material.WOOL, 1, (short) color);
                for (int i = 0; i < 6; i++) {
                    recipe.put(i, wool.clone());
                }
                //木棒
                ItemStack stick = new ItemStack(Material.STICK);
                recipe.put(7, stick);
            } else if (step <= totalStep) {
                //新增Pattern
                //當前banner
                ItemStack prevBanner = new ItemStack(Material.BANNER, 1, banner.getDurability());
                BannerMeta pbm = (BannerMeta) prevBanner.getItemMeta();
                //新增至目前的Pattern
                for (int i = 0; i < step - 2; i++) {
                    pbm.addPattern(bm.getPattern(i));
                }
                prevBanner.setItemMeta(pbm);
                //當前Pattern
                Pattern pattern = bm.getPattern(step - 2);
                //所需染料
                Dye dye = new Dye();
                dye.setColor(pattern.getColor());
                ItemStack dyeItem = dye.toItemStack(1);
                //旗幟位置
                int bannerPosition = 4;
                //染料位置
                List<Integer> dyePosition = Collections.emptyList();
                //根據Pattern決定位置
                switch (pattern.getPattern()) {
                    case SQUARE_BOTTOM_LEFT:
                        dyePosition = Collections.singletonList(6);
                        break;
                    case SQUARE_BOTTOM_RIGHT:
                        dyePosition = Collections.singletonList(8);
                        break;
                    case SQUARE_TOP_LEFT:
                        dyePosition = Collections.singletonList(0);
                        break;
                    case SQUARE_TOP_RIGHT:
                        dyePosition = Collections.singletonList(2);
                        break;
                    case STRIPE_BOTTOM:
                        dyePosition = Arrays.asList(6, 7, 8);
                        break;
                    case STRIPE_TOP:
                        dyePosition = Arrays.asList(0, 1, 2);
                        break;
                    case STRIPE_LEFT:
                        dyePosition = Arrays.asList(0, 3, 6);
                        break;
                    case STRIPE_RIGHT:
                        dyePosition = Arrays.asList(2, 5, 8);
                        break;
                    case STRIPE_CENTER:
                        bannerPosition = 3;
                        dyePosition = Arrays.asList(1, 4, 7);
                        break;
                    case STRIPE_MIDDLE:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(3, 4, 5);
                        break;
                    case STRIPE_DOWNRIGHT:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(0, 4, 8);
                        break;
                    case STRIPE_DOWNLEFT:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(2, 4, 6);
                        break;
                    case STRIPE_SMALL:
                        dyePosition = Arrays.asList(0, 2, 3, 5);
                        break;
                    case CROSS:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(0, 2, 4, 6, 8);
                        break;
                    case STRAIGHT_CROSS:
                        bannerPosition = 0;
                        dyePosition = Arrays.asList(1, 3, 4, 5, 7);
                        break;
                    case TRIANGLE_BOTTOM:
                        bannerPosition = 7;
                        dyePosition = Arrays.asList(4, 6, 8);
                        break;
                    case TRIANGLE_TOP:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(0, 2, 4);
                        break;
                    case TRIANGLES_BOTTOM:
                        dyePosition = Arrays.asList(3, 5, 7);
                        break;
                    case TRIANGLES_TOP:
                        dyePosition = Arrays.asList(1, 3, 5);
                        break;
                    case DIAGONAL_LEFT:
                        dyePosition = Arrays.asList(0, 1, 3);
                        break;
                    case DIAGONAL_RIGHT:
                        dyePosition = Arrays.asList(5, 7, 8);
                        break;
                    case DIAGONAL_LEFT_MIRROR:
                        dyePosition = Arrays.asList(3, 6, 7);
                        break;
                    case DIAGONAL_RIGHT_MIRROR:
                        dyePosition = Arrays.asList(1, 2, 5);
                        break;
                    case CIRCLE_MIDDLE:
                        bannerPosition = 1;
                        dyePosition = Collections.singletonList(4);
                        break;
                    case RHOMBUS_MIDDLE:
                        dyePosition = Arrays.asList(1, 3, 5, 7);
                        break;
                    case HALF_VERTICAL:
                        bannerPosition = 5;
                        dyePosition = Arrays.asList(0, 1, 3, 4, 6, 7);
                        break;
                    case HALF_HORIZONTAL:
                        bannerPosition = 7;
                        dyePosition = Arrays.asList(0, 1, 2, 3, 4, 5);
                        break;
                    case HALF_VERTICAL_MIRROR:
                        bannerPosition = 3;
                        dyePosition = Arrays.asList(1, 2, 4, 5, 7, 8);
                        break;
                    case HALF_HORIZONTAL_MIRROR:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(3, 4, 5, 6, 7, 8);
                        break;
                    case BORDER:
                        dyePosition = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8);
                        break;
                    case CURLY_BORDER:
                        recipe.put(1, new ItemStack(Material.VINE));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case CREEPER:
                        recipe.put(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 4));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case GRADIENT:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(0, 2, 4, 7);
                        break;
                    case GRADIENT_UP:
                        bannerPosition = 7;
                        dyePosition = Arrays.asList(1, 4, 6, 8);
                        break;
                    case BRICKS:
                        recipe.put(1, new ItemStack(Material.BRICK));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case SKULL:
                        recipe.put(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 1));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case FLOWER:
                        recipe.put(1, new ItemStack(Material.RED_ROSE, 1, (short) 8));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case MOJANG:
                        recipe.put(1, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                }
                //放置旗幟與染料
                recipe.put(bannerPosition, prevBanner);
                for (int i : dyePosition) {
                    recipe.put(i, dyeItem.clone());
                }
            }
            //合成結果
            //當前banner
            ItemStack currentBanner = new ItemStack(Material.BANNER, 1, banner.getDurability());
            BannerMeta cbm = (BannerMeta) currentBanner.getItemMeta();
            //新增至目前的Pattern
            for (int i = 0; i < step - 1; i++) {
                cbm.addPattern(bm.getPattern(i));
            }
            currentBanner.setItemMeta(cbm);
            recipe.put(9, currentBanner);
        }
        return recipe;
    }
}
