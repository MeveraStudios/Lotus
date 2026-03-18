package studio.mevera.menus.util;

import lombok.Data;

@Data
public class Couple<L, R> {
    private final L left;
    private final R right;
}
