package serialization.action.management;

/**
 * Created by lollo on 06/06/2017.
 */

public abstract class ActorAnnounceAction extends ManagementAction {

    private static final long serialVersionUID = 6722305545748319370L;
    private int major;
    private int minor;
    private int revision;

    private String name;

    public ActorAnnounceAction() {
    }

    public ActorAnnounceAction(String name) {
        this.name = name;
    }

    public ActorAnnounceAction(String name, int major, int minor, int revision){
        this.name = name;
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }
}
