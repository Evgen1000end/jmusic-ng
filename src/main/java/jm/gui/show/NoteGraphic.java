package jm.gui.show;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class NoteGraphic extends Component implements MouseListener {

  NoteGraphic() {
    super();
    this.addMouseListener(this);
  }

  public void mousePressed(MouseEvent me) {
    System.out.println("X is: " + me.getX());
  }

  public void mouseClicked(MouseEvent me) {
  }

  public void mouseEntered(MouseEvent me) {
  }

  public void mouseExited(MouseEvent me) {
  }

  public void mouseReleased(MouseEvent me) {
  }
}
