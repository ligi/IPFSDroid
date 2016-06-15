package org.ligi.ipfsdroid;

public enum IPFSConnectionMode {
    FullNode(1),
    Simple(0);

    private final int integerRepresentation;

    int getIntegerRepresentation() {
        return integerRepresentation;
    }

    IPFSConnectionMode(final int integerRepresentation) {
        this.integerRepresentation = integerRepresentation;
    }

    static IPFSConnectionMode getModeByIntegerRepresentation(int i) {
        switch (i) {
            case 1:
                return FullNode;
            default:
                return Simple;
        }
    }
}
