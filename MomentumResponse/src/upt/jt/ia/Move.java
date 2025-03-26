package upt.jt.ia;


public class Move
{
    private String action;
    private NodeGameAB node;
    
    public Move( String action, NodeGameAB node)
    {
        this.action = action;
        this.node = node;
    }

    public String getAction() {
        return action;
    }
    
    public NodeGameAB getNode() {
        return node;
    }
    
    public String toString() {
        return "Move: "+ action+"\n"+node.toString();
    }

}
