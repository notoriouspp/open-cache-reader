package org.dreambot.algos.search.astar;

import notoscripts.scripts.pestcontrol.wrapper.IslandTile;
import org.dreambot.api.methods.MethodContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : Notorious
 * @version : 0.0.1
 * @since : 7/2/2015
 * Time : 3:50 PM
 */
public class IslandPath {

    private MethodContext context;
    private List<IslandTile> tiles;

    public IslandPath(MethodContext context) {
        this(context, new ArrayList<>());
    }

    public IslandPath(MethodContext context, List<IslandTile> tiles) {
        this.context = context;
        this.tiles = tiles;
    }

    public void add(IslandTile... tiles){
        if(tiles != null) {
            Collections.addAll(this.tiles, tiles);
        }
    }

    public boolean isEmpty () {
        return tiles.isEmpty();
    }

    public List<IslandTile> getTiles() {
        return tiles;
    }

    public void paint (Graphics2D g) {
        if(!isEmpty()) {
            for (IslandTile tile : tiles) {
                Polygon polygon = context.getMap().getPolygon(tile.toTile(context.getClient()));
                if(polygon != null){
                    g.draw(polygon);
                }
            }
        }
    }
}
