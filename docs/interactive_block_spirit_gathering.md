## 交互方块开发文档示例：聚灵器（BlockSpiritGathering）

本文以 `BlockSpiritGathering` 聚灵器为例，总结在 1.12.2 + RetroFuturaGradle + ModularUI 环境下，**从 0
实现一个“可交互、有存储、有界面”的方块**需要的内容与步骤。

---

## 一、整体结构概览

要做一个带界面的交互方块，通常需要这几部分一起工作：

- **方块类**：`Block` 子类
    - 负责：方块本身的属性（硬度、材质、透明度等）、是否有 TileEntity、右键打开界面等。
- **实体方块（TileEntity）类**：`TileEntity` 子类
    - 负责：**存储数据和逻辑**（物品栏、进度、燃料、tick 更新），以及实现 `IGuiHolder` 以构建 ModularUI 界面。
- **注册代码**：
    - 在 `RegistryBlocks` / `RegistryTiles`（如果有）里**注册方块与 TileEntity**。
- **资源文件**：
    - `blockstates/*.json`：方块状态到模型的映射。
    - `models/block/*.json`：方块 3D 模型。
    - `models/item/*.json`：物品形态的模型（一般 parent 指向方块模型）。
    - `textures/blocks/*.png`：方块纹理。
    - `lang/*.lang`：本地化名字。
- **GUI / 交互层**：
    - 使用 ModularUI：`IGuiHolder.buildUI(...)` 返回 `ModularPanel`，由 `TileEntityGuiFactory.open(player, pos)` 打开。
    - 如需与原版 Container 兼容，还可以写 `Container` / `GuiContainer`（你现在主要用 ModularUI）。
- **自动化与能力（可选但常用）**：
    - 使用 `ItemStackHandler` + `CapabilityItemHandler.ITEM_HANDLER_CAPABILITY`，控制漏斗 / 管线如何与方块交互。

---

## 二、聚灵器当前实现拆解

### 1. 方块类：`BlockSpiritGathering`

- 位置：`cn.chahuyun.enchantment.block.BlockSpiritGathering`
- 主要职责：
    - 继承 `Block`，指定材质、抗性、声音、创造栏。
    - `hasTileEntity` / `createTileEntity` 返回对应的实体方块 `EntitySpiritGathering`。
    - 设置透明渲染相关属性（CUTOUT 层、不不透明、光线可穿透）。

关键点（示例）：

- 注册名 / 语言键通过静态 `getName()` 统一：
    - 注册名：`enchantment:spirit_gathering`
    - 语言键：`tile.enchantment.spirit_gathering.name`
- 渲染相关：
    - `getRenderLayer()` 返回 `BlockRenderLayer.CUTOUT`（或者 `TRANSLUCENT`）。
    - `isOpaqueCube` / `isFullCube` 返回 `false`。
    - 构造中 `setLightOpacity(0)`，让光线可以穿透中空部分。

### 2. 实体方块类：`EntitySpiritGathering`

- 位置：`cn.chahuyun.enchantment.entity.block.EntitySpiritGathering`
- 继承关系：`AbstractEntityInventory`（自写基类） + `ITickable` + `IGuiHolder`
- 主要职责：
    - 持有一个物品栏（`AbstractEntityInventory` 内部一般是 `ItemStackHandler`）。
    - tick 逻辑（`update()`）——目前你这里还是空的，可以后续加“聚灵”过程。
    - NBT 读写：在 `readFromNBT` / `writeToNBT` 中保存物品栏与进度。
    - 实现 `IGuiHolder.buildUI(...)`，构建 ModularUI 的界面布局。

GUI 示例（简化说明）：

- `buildUI` 中创建 `ModularPanel.defaultPanel(getName())`
- 使用 `panel.bindPlayerInventory()` 绑定玩家背包显示
- 使用 `ItemSlot().slot(inventory, index)` 在面板上放置物品格
- 可加入进度条、按钮等组件

### 3. 注册：方块 & 实体方块

- **方块注册**：在 `RegistryBlocks`（或类似类）里，创建 `BlockSpiritGathering` 实例并注册到 `GameRegistry`。
- **TileEntity 注册**：在模组初始化阶段，通过
  `GameRegistry.registerTileEntity(EntitySpiritGathering.class, new ResourceLocation(modid, "spirit_gathering"))` 注册。

注意：RetroFuturaGradle + 1.12.2 环境下，很多注册逻辑会放在所谓的“注册事件”或专门类中，保持与现有结构一致即可。

### 4. 资源：blockstates / models / textures / lang

- `blockstates/spirit_gathering.json`
    - 把方块默认状态映射到方块模型 `enchantment:spirit_gathering`。
- `models/block/spirit_gathering.json`
    - 使用 Blockbench 导出的自定义立方体模型。
    - 注意要符合 1.12.2 的 **vanilla block model 格式**：
        - `elements` 内每个立方体用 `from` / `to` / `faces` 描述。
        - 如果有 `rotation`，必须是 `{ "origin": [...], "axis": "x|y|z", "angle": 值 }` 格式；
        - 你之前使用了 `"rotation": { "x": 0, "y": 0, "z": 0, ... }`（新版本格式），1.12.2 无法解析，日志报 `Missing axis`
          ，导致模型直接加载失败。
        - 我已经帮你把这些 `rotation` 字段移除，保留 `light_emission` 与 `faces`，现在模型可以正常被 1.12.2 解析。
- `models/item/spirit_gathering.json`
    - 一般写成 `{"parent": "enchantment:block/spirit_gathering"}`，共享同一套模型。
- `textures/blocks/*.png`
    - `obsidian.png`、`glass.png`、`obsidian_gold.png` 等，对应模型里 `enchantment:blocks/...` 的纹理引用。
- `lang/zh_cn.lang` / `lang/en_us.lang`
    - 提供展示名称，例如：`tile.enchantment.spirit_gathering.name=聚灵器`。

### 5. 打开界面：Block → TileEntity → ModularUI

以钢制熔炉为例，你已经有一套流程，可以直接复用到聚灵器：

1. 方块 `onBlockActivated` 中：
    - `TileEntity tile = world.getTileEntity(pos);`
    -
    `if (tile instanceof EntitySpiritGathering && !world.isRemote) { TileEntityGuiFactory.open(player, pos); return true; }`
2. `EntitySpiritGathering` 实现 `IGuiHolder`：
    - `buildUI(GuiData, GuiSyncManager)` 返回 `ModularPanel`，里面用 `ItemSlot` 布局格子，用 `ProgressWidget` 等组件展示状态。
3. ModularUI 会根据 `IGuiHolder` 自动创建客户端界面和同步数据，无需传统的 `Container` / `GuiContainer`。

### 6. 存储与自动化（可按需加入）

如果聚灵器需要：

- **内部物品存储**：
    - 使用 `ItemStackHandler`，在 NBT 里用 `serializeNBT()` / `deserializeNBT()` 存读。
    - 通过 `IInventory` / `ISidedInventory` / `CapabilityItemHandler` 暴露给 GUI 与漏斗。
- **漏斗自动化规则**（类似钢制熔炉）：
    - 在 `hasCapability` / `getCapability` 中返回 `IItemHandler`，按 slot 和 item 类型控制能否插入/取出。
    - 或实现 `ISidedInventory` + `SidedInvWrapper`，按方向划分“输入口”、“燃料口”、“输出口”。
- **Shift+左键快速移动**：
    - 在对应 `Container`（如果有）里重写 `transferStackInSlot`，按“可熔炼物 → 原料槽 / 燃料 → 燃料槽”的规则 merge。

---

## 三、从 0 新建一个交互方块的步骤清单（参考聚灵器）

1. **创建方块类** `BlockXxx`：
    - 继承 `Block`，设置材质、硬度、抗性、声音、creativeTab。
    - 实现 `hasTileEntity` / `createTileEntity` → 返回 `EntityXxx`。
    - 如需要透明或非完整立方体，重写 `getRenderLayer` / `isOpaqueCube` / `isFullCube`，并设置 `setLightOpacity(0)`。
2. **创建实体方块类** `EntityXxx`：
    - 继承 `TileEntity`，视需求实现 `ITickable`、`IInventory` / `IGuiHolder` 等接口。
    - 内部维护 `ItemStackHandler` 或自定义物品栏。
    - 在 `readFromNBT` / `writeToNBT` 里序列化状态。
    - 实现 `buildUI`（如果用 ModularUI）构建界面。
3. **在注册类中注册方块和 TileEntity**：
    - `GameRegistry.register(blockXxx)` 或对应的事件注册写法。
    - `GameRegistry.registerTileEntity(EntityXxx.class, new ResourceLocation(modid, "xxx"))`。
4. **添加资源文件**：
    - `blockstates/xxx.json`
    - `models/block/xxx.json`（注意 1.12.2 的模型 JSON 格式，不要使用高版本 Blockbench 特有字段格式）
    - `models/item/xxx.json`（parent 指向 block 模型）
    - `textures/blocks/xxx.png`
    - `lang` 中加入名字。
5. **在方块类中实现右键交互逻辑**：
    - 服务端 `onBlockActivated` 中打开 GUI（ModularUI 的 `TileEntityGuiFactory.open` 或传统 `player.openGui`）。
6. **（可选）实现自动化与 shift 行为**：
    - `CapabilityItemHandler` / `ISidedInventory` 控制漏斗与管线的行为。
    - `Container.transferStackInSlot` 控制 GUI 里的 shift+左键逻辑。

---

## 四、本次聚灵器模型加载失败的具体原因总结

- 报错核心：
    - 日志中有 `Missing axis, expected to find a string`，并指向 `enchantment:block/spirit_gathering`。
    - 说明模型 JSON 里的 `rotation` 字段不符合 1.12.2 的旧版格式。
- 你的模型使用了新版本 Blockbench 的导出格式：
    - `"rotation": {"x": 0, "y": 0, "z": 0, "origin": [...]}`
    - 但 1.12.2 期望的是 `"rotation": {"origin": [...], "axis": "x|y|z", "angle": ...}` 或直接没有 `rotation`。
- 由于这些 `rotation` 其实都是 0°，**删除这些字段即可**，我已经在 `models/block/spirit_gathering.json` 中移除了这两处
  `rotation`，模型就可以被正常加载了。


