package net.minecraft.command;

import com.google.common.primitives.Doubles;
import java.util.Collections;
import com.mojang.brigadier.Message;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import net.minecraft.util.math.MathHelper;
import java.util.function.ToDoubleFunction;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.function.Function;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.EntityType;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.NumberRange;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import java.util.function.BiConsumer;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class EntitySelectorReader
{
    public static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION;
    public static final DynamicCommandExceptionType UNKNOWN_SELECTOR_EXCEPTION;
    public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION;
    public static final SimpleCommandExceptionType MISSING_EXCEPTION;
    public static final SimpleCommandExceptionType UNTERMINATED_EXCEPTION;
    public static final DynamicCommandExceptionType VALUELESS_EXCEPTION;
    public static final BiConsumer<Vec3d, List<? extends Entity>> UNSORTED;
    public static final BiConsumer<Vec3d, List<? extends Entity>> NEAREST_FIRST;
    public static final BiConsumer<Vec3d, List<? extends Entity>> FURTHEST_FIRST;
    public static final BiConsumer<Vec3d, List<? extends Entity>> RANDOM;
    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> DEFAULT_SUGGESTION_PROVIDER;
    private final StringReader reader;
    private final boolean m;
    private int count;
    private boolean includingNonPlayer;
    private boolean localWorldOnly;
    private NumberRange.FloatRange distance;
    private NumberRange.IntRange experienceRange;
    @Nullable
    private Double offsetX;
    @Nullable
    private Double offsetY;
    @Nullable
    private Double offsetZ;
    @Nullable
    private Double boxX;
    @Nullable
    private Double boxY;
    @Nullable
    private Double boxZ;
    private FloatRange pitchRange;
    private FloatRange yawRange;
    private Predicate<Entity> predicate;
    private BiConsumer<Vec3d, List<? extends Entity>> sorter;
    private boolean senderOnly;
    @Nullable
    private String playerName;
    private int startCursor;
    @Nullable
    private UUID uuid;
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionProvider;
    private boolean H;
    private boolean I;
    private boolean J;
    private boolean K;
    private boolean L;
    private boolean M;
    private boolean N;
    private boolean O;
    @Nullable
    private EntityType<?> entityType;
    private boolean Q;
    private boolean R;
    private boolean S;
    private boolean checkPermissions;
    
    public EntitySelectorReader(final StringReader stringReader) {
        this(stringReader, true);
    }
    
    public EntitySelectorReader(final StringReader stringReader, final boolean boolean2) {
        this.distance = NumberRange.FloatRange.ANY;
        this.experienceRange = NumberRange.IntRange.ANY;
        this.pitchRange = FloatRange.ANY;
        this.yawRange = FloatRange.ANY;
        this.predicate = (entity -> true);
        this.sorter = EntitySelectorReader.UNSORTED;
        this.suggestionProvider = EntitySelectorReader.DEFAULT_SUGGESTION_PROVIDER;
        this.reader = stringReader;
        this.m = boolean2;
    }
    
    public EntitySelector build() {
        BoundingBox boundingBox1;
        if (this.boxX != null || this.boxY != null || this.boxZ != null) {
            boundingBox1 = this.createBox((this.boxX == null) ? 0.0 : ((double)this.boxX), (this.boxY == null) ? 0.0 : ((double)this.boxY), (this.boxZ == null) ? 0.0 : ((double)this.boxZ));
        }
        else if (this.distance.getMax() != null) {
            final float float2 = this.distance.getMax();
            boundingBox1 = new BoundingBox(-float2, -float2, -float2, float2 + 1.0f, float2 + 1.0f, float2 + 1.0f);
        }
        else {
            boundingBox1 = null;
        }
        Function<Vec3d, Vec3d> function2;
        if (this.offsetX == null && this.offsetY == null && this.offsetZ == null) {
            function2 = (Function<Vec3d, Vec3d>)(vec3d -> vec3d);
        }
        else {
            final Vec3d vec3d2;
            function2 = (Function<Vec3d, Vec3d>)(vec3d -> {
                new Vec3d((this.offsetX == null) ? vec3d.x : this.offsetX, (this.offsetY == null) ? vec3d.y : this.offsetY, (this.offsetZ == null) ? vec3d.z : this.offsetZ);
                return vec3d2;
            });
        }
        return new EntitySelector(this.count, this.includingNonPlayer, this.localWorldOnly, this.predicate, this.distance, function2, boundingBox1, this.sorter, this.senderOnly, this.playerName, this.uuid, this.entityType, this.checkPermissions);
    }
    
    private BoundingBox createBox(final double x, final double y, final double z) {
        final boolean boolean7 = x < 0.0;
        final boolean boolean8 = y < 0.0;
        final boolean boolean9 = z < 0.0;
        final double double10 = boolean7 ? x : 0.0;
        final double double11 = boolean8 ? y : 0.0;
        final double double12 = boolean9 ? z : 0.0;
        final double double13 = (boolean7 ? 0.0 : x) + 1.0;
        final double double14 = (boolean8 ? 0.0 : y) + 1.0;
        final double double15 = (boolean9 ? 0.0 : z) + 1.0;
        return new BoundingBox(double10, double11, double12, double13, double14, double15);
    }
    
    private void buildPredicate() {
        if (this.pitchRange != FloatRange.ANY) {
            this.predicate = this.predicate.and(this.rotationPredicate(this.pitchRange, entity -> entity.pitch));
        }
        if (this.yawRange != FloatRange.ANY) {
            this.predicate = this.predicate.and(this.rotationPredicate(this.yawRange, entity -> entity.yaw));
        }
        if (!this.experienceRange.isDummy()) {
            this.predicate = this.predicate.and(entity -> entity instanceof ServerPlayerEntity && this.experienceRange.test(entity.experience));
        }
    }
    
    private Predicate<Entity> rotationPredicate(final FloatRange floatRange, final ToDoubleFunction<Entity> toDoubleFunction) {
        final double double3 = MathHelper.wrapDegrees((floatRange.getMin() == null) ? 0.0f : ((float)floatRange.getMin()));
        final double double4 = MathHelper.wrapDegrees((floatRange.getMax() == null) ? 359.0f : ((float)floatRange.getMax()));
        final double double5;
        final double n;
        final double n2;
        return entity6 -> {
            double5 = MathHelper.wrapDegrees(toDoubleFunction.applyAsDouble(entity6));
            if (n > n2) {
                return double5 >= n || double5 <= n2;
            }
            else {
                return double5 >= n && double5 <= n2;
            }
        };
    }
    
    protected void readAtSelector() throws CommandSyntaxException {
        this.checkPermissions = true;
        this.suggestionProvider = this::suggestSelectorRest;
        if (!this.reader.canRead()) {
            throw EntitySelectorReader.MISSING_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        final int integer1 = this.reader.getCursor();
        final char character2 = this.reader.read();
        if (character2 == 'p') {
            this.count = 1;
            this.includingNonPlayer = false;
            this.sorter = EntitySelectorReader.NEAREST_FIRST;
            this.setEntityType(EntityType.PLAYER);
        }
        else if (character2 == 'a') {
            this.count = Integer.MAX_VALUE;
            this.includingNonPlayer = false;
            this.sorter = EntitySelectorReader.UNSORTED;
            this.setEntityType(EntityType.PLAYER);
        }
        else if (character2 == 'r') {
            this.count = 1;
            this.includingNonPlayer = false;
            this.sorter = EntitySelectorReader.RANDOM;
            this.setEntityType(EntityType.PLAYER);
        }
        else if (character2 == 's') {
            this.count = 1;
            this.includingNonPlayer = true;
            this.senderOnly = true;
        }
        else {
            if (character2 != 'e') {
                this.reader.setCursor(integer1);
                throw EntitySelectorReader.UNKNOWN_SELECTOR_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, ('@' + String.valueOf(character2)));
            }
            this.count = Integer.MAX_VALUE;
            this.includingNonPlayer = true;
            this.sorter = EntitySelectorReader.UNSORTED;
            this.predicate = Entity::isAlive;
        }
        this.suggestionProvider = this::suggestOpen;
        if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestionProvider = this::suggestOptionOrEnd;
            this.readOption();
        }
    }
    
    protected void readRegular() throws CommandSyntaxException {
        if (this.reader.canRead()) {
            this.suggestionProvider = this::suggestNormal;
        }
        final int integer1 = this.reader.getCursor();
        final String string2 = this.reader.readString();
        try {
            this.uuid = UUID.fromString(string2);
            this.includingNonPlayer = true;
        }
        catch (IllegalArgumentException illegalArgumentException3) {
            if (string2.isEmpty() || string2.length() > 16) {
                this.reader.setCursor(integer1);
                throw EntitySelectorReader.INVALID_ENTITY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
            }
            this.includingNonPlayer = false;
            this.playerName = string2;
        }
        this.count = 1;
    }
    
    protected void readOption() throws CommandSyntaxException {
        this.suggestionProvider = this::suggestOption;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            final int integer1 = this.reader.getCursor();
            final String string2 = this.reader.readString();
            final EntitySelectorOptions.SelectorHandler selectorHandler3 = EntitySelectorOptions.getHandler(this, string2, integer1);
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                this.reader.setCursor(integer1);
                throw EntitySelectorReader.VALUELESS_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, string2);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestionProvider = EntitySelectorReader.DEFAULT_SUGGESTION_PROVIDER;
            selectorHandler3.handle(this);
            this.reader.skipWhitespace();
            this.suggestionProvider = this::suggestEndNext;
            if (!this.reader.canRead()) {
                continue;
            }
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestionProvider = this::suggestOption;
            }
            else {
                if (this.reader.peek() == ']') {
                    break;
                }
                throw EntitySelectorReader.UNTERMINATED_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
            }
        }
        if (this.reader.canRead()) {
            this.reader.skip();
            this.suggestionProvider = EntitySelectorReader.DEFAULT_SUGGESTION_PROVIDER;
            return;
        }
        throw EntitySelectorReader.UNTERMINATED_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
    }
    
    public boolean readNegationCharacter() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '!') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }
    
    public boolean readTagCharacter() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }
    
    public StringReader getReader() {
        return this.reader;
    }
    
    public void setPredicate(final Predicate<Entity> predicate) {
        this.predicate = this.predicate.and(predicate);
    }
    
    public void setLocalWorldOnly() {
        this.localWorldOnly = true;
    }
    
    public NumberRange.FloatRange getDistance() {
        return this.distance;
    }
    
    public void setDistance(final NumberRange.FloatRange distance) {
        this.distance = distance;
    }
    
    public NumberRange.IntRange getExperienceRange() {
        return this.experienceRange;
    }
    
    public void setExperienceRange(final NumberRange.IntRange experienceRange) {
        this.experienceRange = experienceRange;
    }
    
    public FloatRange getPitchRange() {
        return this.pitchRange;
    }
    
    public void setPitchRange(final FloatRange floatRange) {
        this.pitchRange = floatRange;
    }
    
    public FloatRange getYawRange() {
        return this.yawRange;
    }
    
    public void setYawRange(final FloatRange floatRange) {
        this.yawRange = floatRange;
    }
    
    @Nullable
    public Double getOffsetX() {
        return this.offsetX;
    }
    
    @Nullable
    public Double getOffsetY() {
        return this.offsetY;
    }
    
    @Nullable
    public Double getOffsetZ() {
        return this.offsetZ;
    }
    
    public void setOffsetX(final double offsetX) {
        this.offsetX = offsetX;
    }
    
    public void setOffsetY(final double offsetY) {
        this.offsetY = offsetY;
    }
    
    public void setOffsetZ(final double offsetZ) {
        this.offsetZ = offsetZ;
    }
    
    public void setBoxX(final double boxX) {
        this.boxX = boxX;
    }
    
    public void setBoxY(final double boxY) {
        this.boxY = boxY;
    }
    
    public void setBoxZ(final double boxZ) {
        this.boxZ = boxZ;
    }
    
    @Nullable
    public Double getBoxX() {
        return this.boxX;
    }
    
    @Nullable
    public Double getBoxY() {
        return this.boxY;
    }
    
    @Nullable
    public Double getBoxZ() {
        return this.boxZ;
    }
    
    public void setCount(final int count) {
        this.count = count;
    }
    
    public void setIncludingNonPlayer(final boolean includingNonPlayer) {
        this.includingNonPlayer = includingNonPlayer;
    }
    
    public void setSorter(final BiConsumer<Vec3d, List<? extends Entity>> sorter) {
        this.sorter = sorter;
    }
    
    public EntitySelector read() throws CommandSyntaxException {
        this.startCursor = this.reader.getCursor();
        this.suggestionProvider = this::suggestSelector;
        if (this.reader.canRead() && this.reader.peek() == '@') {
            if (!this.m) {
                throw EntitySelectorReader.NOT_ALLOWED_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
            }
            this.reader.skip();
            this.readAtSelector();
        }
        else {
            this.readRegular();
        }
        this.buildPredicate();
        return this.build();
    }
    
    private static void suggestSelector(final SuggestionsBuilder suggestionsBuilder) {
        suggestionsBuilder.suggest("@p", (Message)new TranslatableTextComponent("argument.entity.selector.nearestPlayer", new Object[0]));
        suggestionsBuilder.suggest("@a", (Message)new TranslatableTextComponent("argument.entity.selector.allPlayers", new Object[0]));
        suggestionsBuilder.suggest("@r", (Message)new TranslatableTextComponent("argument.entity.selector.randomPlayer", new Object[0]));
        suggestionsBuilder.suggest("@s", (Message)new TranslatableTextComponent("argument.entity.selector.self", new Object[0]));
        suggestionsBuilder.suggest("@e", (Message)new TranslatableTextComponent("argument.entity.selector.allEntities", new Object[0]));
    }
    
    private CompletableFuture<Suggestions> suggestSelector(final SuggestionsBuilder suggestionsBuilder, final Consumer<SuggestionsBuilder> consumer) {
        consumer.accept(suggestionsBuilder);
        if (this.m) {
            suggestSelector(suggestionsBuilder);
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestNormal(final SuggestionsBuilder suggestionsBuilder, final Consumer<SuggestionsBuilder> consumer) {
        final SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.startCursor);
        consumer.accept(suggestionsBuilder2);
        return (CompletableFuture<Suggestions>)suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestSelectorRest(final SuggestionsBuilder suggestionsBuilder, final Consumer<SuggestionsBuilder> consumer) {
        final SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(suggestionsBuilder.getStart() - 1);
        suggestSelector(suggestionsBuilder2);
        suggestionsBuilder.add(suggestionsBuilder2);
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestOpen(final SuggestionsBuilder suggestionsBuilder, final Consumer<SuggestionsBuilder> consumer) {
        suggestionsBuilder.suggest(String.valueOf('['));
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestOptionOrEnd(final SuggestionsBuilder suggestionsBuilder, final Consumer<SuggestionsBuilder> consumer) {
        suggestionsBuilder.suggest(String.valueOf(']'));
        EntitySelectorOptions.suggestOptions(this, suggestionsBuilder);
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestOption(final SuggestionsBuilder suggestionsBuilder, final Consumer<SuggestionsBuilder> consumer) {
        EntitySelectorOptions.suggestOptions(this, suggestionsBuilder);
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestEndNext(final SuggestionsBuilder suggestionsBuilder, final Consumer<SuggestionsBuilder> consumer) {
        suggestionsBuilder.suggest(String.valueOf(','));
        suggestionsBuilder.suggest(String.valueOf(']'));
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    public boolean isSenderOnly() {
        return this.senderOnly;
    }
    
    public void setSuggestionProvider(final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> biFunction) {
        this.suggestionProvider = biFunction;
    }
    
    public CompletableFuture<Suggestions> listSuggestions(final SuggestionsBuilder suggestionsBuilder, final Consumer<SuggestionsBuilder> consumer) {
        return this.suggestionProvider.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
    }
    
    public boolean v() {
        return this.H;
    }
    
    public void c(final boolean boolean1) {
        this.H = boolean1;
    }
    
    public boolean w() {
        return this.I;
    }
    
    public void d(final boolean boolean1) {
        this.I = boolean1;
    }
    
    public boolean x() {
        return this.J;
    }
    
    public void e(final boolean boolean1) {
        this.J = boolean1;
    }
    
    public boolean y() {
        return this.K;
    }
    
    public void f(final boolean boolean1) {
        this.K = boolean1;
    }
    
    public boolean z() {
        return this.L;
    }
    
    public void g(final boolean boolean1) {
        this.L = boolean1;
    }
    
    public boolean A() {
        return this.M;
    }
    
    public void h(final boolean boolean1) {
        this.M = boolean1;
    }
    
    public boolean B() {
        return this.N;
    }
    
    public void i(final boolean boolean1) {
        this.N = boolean1;
    }
    
    public void j(final boolean boolean1) {
        this.O = boolean1;
    }
    
    public void setEntityType(final EntityType<?> entityType) {
        this.entityType = entityType;
    }
    
    public void D() {
        this.Q = true;
    }
    
    public boolean hasEntityType() {
        return this.entityType != null;
    }
    
    public boolean F() {
        return this.Q;
    }
    
    public boolean G() {
        return this.R;
    }
    
    public void k(final boolean boolean1) {
        this.R = boolean1;
    }
    
    public boolean H() {
        return this.S;
    }
    
    public void l(final boolean boolean1) {
        this.S = boolean1;
    }
    
    static {
        INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.invalid", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_SELECTOR_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.entity.selector.unknown", new Object[] { object });
            return translatableTextComponent;
        });
        NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.selector.not_allowed", new Object[0]));
        MISSING_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.selector.missing", new Object[0]));
        UNTERMINATED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("argument.entity.options.unterminated", new Object[0]));
        final TranslatableTextComponent translatableTextComponent2;
        VALUELESS_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.entity.options.valueless", new Object[] { object });
            return translatableTextComponent2;
        });
        UNSORTED = ((vec3d, list) -> {});
        NEAREST_FIRST = ((vec3d, list) -> list.sort((entity2, entity3) -> Doubles.compare(entity2.squaredDistanceTo(vec3d), entity3.squaredDistanceTo(vec3d))));
        FURTHEST_FIRST = ((vec3d, list) -> list.sort((entity2, entity3) -> Doubles.compare(entity3.squaredDistanceTo(vec3d), entity2.squaredDistanceTo(vec3d))));
        RANDOM = ((vec3d, list) -> Collections.shuffle(list));
        DEFAULT_SUGGESTION_PROVIDER = ((suggestionsBuilder, consumer) -> suggestionsBuilder.buildFuture());
    }
}
