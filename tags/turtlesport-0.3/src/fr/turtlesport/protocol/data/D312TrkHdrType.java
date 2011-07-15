package fr.turtlesport.protocol.data;

import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// bool dspl; /* display on the map? */
// uint8 color; /* color (see below) */
// /* char trk_ident[]; null-terminated string */
// } D312_Trk_Hdr_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D312TrkHdrType extends D310TrkHdrType {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D312TrkHdrType.class);
  }

  public D312TrkHdrType() {
    log.debug(">>D312TrkPointType");
    log.debug("<<D312TrkPointType");
  }

}
