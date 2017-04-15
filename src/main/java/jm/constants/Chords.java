/*
 * Chords.java 0.1.0.3 5th November 2004
 *
 * Copyright (C) 2004 David Turner
 *
 * <This Java Class is part of the jMusic API version 1.4, December 2003.>
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
package jm.constants;

public interface Chords {

  /**
   * chord interval patterns
   */
  int[] MAJOR = {4, 7};
  int[] MINOR = {3, 7};
  int[] AUGMENTED = {4, 8};
  int[] DIMINISHED = {3, 6};
  int[] SUSPENDED_FOURTH = {5, 7};
  int[] FLATTED_FIFTH = {4, 6};
  int[] SIXTH = {4, 7, 9};
  int[] MINOR_SIXTH = {3, 7, 9};
  int[] SEVENTH = {4, 7, 10};
  int[] MINOR_SEVENTH = {3, 7, 10};
  int[] MAJOR_SEVENTH = {4, 7, 11};
  int[] SEVENTH_SHARP_FIFTH = {4, 8, 10};
  int[] DIMINISHED_SEVENTH = {4, 6, 9};
  int[] SEVENTH_FLAT_FIFTH = {4, 6, 10};
  int[] MINOR_SEVENTH_FLAT_FIFTH = {3, 6, 10};
  int[] SIXTH_ADDED_NINTH = {4, 7, 9, 14};
  int[] SEVENTH_SHARP_NINTH = {4, 7, 10, 15};
  int[] SEVENTH_FLAT_NINTH = {4, 7, 10, 13};
  int[] NINTH = {4, 7, 10, 14};
  int[] MINOR_NINTH = {3, 7, 10, 14};
  int[] ELEVENTH = {7, 10, 14, 17};
  int[] MINOR_ELEVENTH = {3, 7, 10, 14, 17};
  int[] THIRTEENTH = {4, 7, 10, 14, 21};
}
