package beloo.recyclerviewcustomadapter;

public interface IChildGravityResolver {
    @SpanLayoutChildGravity
    int getItemGravity(int position);
}
