package me.shedaniel.clothconfig2.gui.entries;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IntegerListListEntry extends BaseListEntry<Integer, IntegerListListEntry.IntegerListCell> {
    
    private int minimum, maximum;
    private Function<Integer, Optional<String>> cellErrorSupplier;
    
    @Deprecated
    public IntegerListListEntry(String fieldName, List<Integer> value, boolean defaultExpended, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Integer>> saveConsumer, Supplier<List<Integer>> defaultValue, String resetButtonKey) {
        this(fieldName, value, defaultExpended, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
    }
    
    @Deprecated
    public IntegerListListEntry(String fieldName, List<Integer> value, boolean defaultExpended, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Integer>> saveConsumer, Supplier<List<Integer>> defaultValue, String resetButtonKey, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, defaultValue, baseListEntry -> new IntegerListCell(0, (IntegerListListEntry) baseListEntry), saveConsumer, resetButtonKey, requiresRestart);
        this.minimum = -Integer.MAX_VALUE;
        this.maximum = Integer.MAX_VALUE;
        for(int integer : value)
            cells.add(new IntegerListCell(integer, this));
        this.widgets.addAll(cells);
        expended = defaultExpended;
    }
    
    public Function<Integer, Optional<String>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public void setCellErrorSupplier(Function<Integer, Optional<String>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
    }
    
    @Override
    public List<Integer> getValue() {
        return cells.stream().map(IntegerListCell::getValue).collect(Collectors.toList());
    }
    
    public IntegerListListEntry setMaximum(int maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public IntegerListListEntry setMinimum(int minimum) {
        this.minimum = minimum;
        return this;
    }
    
    @Override
    protected IntegerListCell getFromValue(Integer value) {
        return new IntegerListCell(value, this);
    }
    
    public static class IntegerListCell extends BaseListCell {
        
        private Function<String, String> stripCharacters = s -> {
            StringBuilder stringBuilder_1 = new StringBuilder();
            char[] var2 = s.toCharArray();
            int var3 = var2.length;
            
            for(int var4 = 0; var4 < var3; ++var4)
                if (Character.isDigit(var2[var4]) || var2[var4] == '-')
                    stringBuilder_1.append(var2[var4]);
            
            return stringBuilder_1.toString();
        };
        private TextFieldWidget widget;
        private boolean isSelected;
        private IntegerListListEntry listListEntry;
        
        public IntegerListCell(int value, IntegerListListEntry listListEntry) {
            this.listListEntry = listListEntry;
            this.setErrorSupplier(() -> listListEntry.cellErrorSupplier == null ? Optional.empty() : listListEntry.getCellErrorSupplier().apply(getValue()));
            widget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 18, "") {
                @Override
                public void render(int int_1, int int_2, float float_1) {
                    boolean f = isFocused();
                    setFocused(isSelected);
                    widget.setEditableColor(getPreferredTextColor());
                    super.render(int_1, int_2, float_1);
                    setFocused(f);
                }
                
                @Override
                public void write(String string_1) {
                    super.write(stripCharacters.apply(string_1));
                }
            };
            widget.setMaxLength(999999);
            widget.setHasBorder(false);
            widget.setText(value + "");
            widget.setChangedListener(s -> {
                if (!(value + "").equalsIgnoreCase(s))
                    listListEntry.getScreen().setEdited(true, listListEntry.isRequiresRestart());
            });
        }
        
        public int getValue() {
            try {
                return Integer.valueOf(widget.getText());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        @Override
        public Optional<String> getError() {
            try {
                int i = Integer.valueOf(widget.getText());
                if (i > listListEntry.maximum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_large", listListEntry.maximum));
                else if (i < listListEntry.minimum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_small", listListEntry.minimum));
            } catch (NumberFormatException ex) {
                return Optional.of(I18n.translate("text.cloth-config.error.not_valid_number_int"));
            }
            return Optional.empty();
        }
        
        @Override
        public int getCellHeight() {
            return 20;
        }
        
        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            widget.setWidth(entryWidth - 12);
            widget.x = x;
            widget.y = y + 1;
            widget.setEditable(listListEntry.isEditable());
            this.isSelected = isSelected;
            widget.render(mouseX, mouseY, delta);
            if (isSelected && listListEntry.isEditable())
                fill(x, y + 12, x + entryWidth - 12, y + 13, getConfigError().isPresent() ? 0xffff5555 : 0xffe0e0e0);
        }
        
        @Override
        public List<? extends Element> children() {
            return Collections.singletonList(widget);
        }
        
    }
    
}
