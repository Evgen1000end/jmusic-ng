/*
 * Images.java 0.0.1 8th July 2001
 *
 * Copyright (C) 2001 Adam Kirby
 *
 * <This Java Class is part of the jMusic API version 1.5, March 2004.>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.gui.cpn;

import java.awt.*;

/**
 * Interface with methods for returning Images representing notes, rests and
 * other stave elements for use with a CPN {@link Stave}.
 * <p/>
 * This abstraction is mainly used to allow different implementations from
 * loading the Images in different ways.  For instance, the {@link
 * ToolkitImages} implementation is suitable for applications, while the {@link
 * AppletImages} implementation is suitable for use in applets.
 *
 * @author Adam Kirby
 * @version 0.0.1, 8th July 2001
 * @see Stave
 * @see ToolkitImages
 * @see AppletImages
 */
public interface Images {

  /**
   * Returns an Image representing a treble clef.
   */
  Image getTrebleClef();

  /**
   * Returns an Image representing a bass clef.
   */
  Image getBassClef();

  /**
   * Returns an Image representing a semibreve.
   */
  Image getSemibreve();

  /**
   * Returns an Image representing a minim with stem raised.
   */
  Image getMinimUp();

  /**
   * Returns an Image representing a minim with stem lowered.
   */
  Image getMinimDown();

  /**
   * Returns an Image representing a crotchet with stem raised.
   */
  Image getCrotchetUp();

  /**
   * Returns an Image representing a crotchet with stem lowered.
   */
  Image getCrotchetDown();

  /**
   * Returns an Image representing a quaver with stem raised.
   */
  Image getQuaverUp();

  /**
   * Returns an Image representing a quaver with stem lowered.
   */
  Image getQuaverDown();

  /**
   * Returns an Image representing a semiquaver with stem raised.
   */
  Image getSemiquaverUp();

  /**
   * Returns an Image representing a semiquaver with stem lowered.
   */
  Image getSemiquaverDown();

  /**
   * Returns an Image representing a semibreve rest.
   */
  Image getSemibreveRest();

  /**
   * Returns an Image representing a minim rest.
   */
  Image getMinimRest();

  /**
   * Returns an Image representing a crotchet rest.
   */
  Image getCrotchetRest();

  /**
   * Returns an Image representing a quaver rest.
   */
  Image getQuaverRest();

  /**
   * Returns an Image representing a semiquaver rest.
   */
  Image getSemiquaverRest();

  /**
   * Returns an Image representing the dot symbol used to indicate the
   * duration of the preceeding symbol should be about half as long again.
   */
  Image getDot();

  /**
   * Returns an Image representing a sharp symbol.
   */
  Image getSharp();

  /**
   * Returns an Image representing a flat symbol.
   */
  Image getFlat();

  /**
   * Returns an Image representing a natural symbol.
   */
  Image getNatural();

  /**
   * Returns an Image representing the digit '1' used in time signatures.
   */
  Image getOne();

  /**
   * Returns an Image representing the digit '2' used in time signatures.
   */
  Image getTwo();

  /**
   * Returns an Image representing the digit '3' used in time signatures.
   */
  Image getThree();

  /**
   * Returns an Image representing the digit '4' used in time signatures.
   */
  Image getFour();

  /**
   * Returns an Image representing the digit '5' used in time signatures.
   */
  Image getFive();

  /**
   * Returns an Image representing the digit '6' used in time signatures.
   */
  Image getSix();

  /**
   * Returns an Image representing the digit '7' used in time signatures.
   */
  Image getSeven();

  /**
   * Returns an Image representing the digit '8' used in time signatures.
   */
  Image getEight();

  /**
   * Returns an Image representing the digit '9' used in time signatures.
   */
  Image getNine();

  /**
   * Returns an Image representing a symbol unique to JMusic which indicates
   * that the selected symbol/note/rest should be deleted.
   */
  Image getDelete();

  /**
   * Returns an Image representing a tie used with notes that have stems
   * lowered.
   */
  Image getTieOver();

  /**
   * Returns an Image representing a tie used with notes that have stems
   * raised.
   */
  Image getTieUnder();

}
