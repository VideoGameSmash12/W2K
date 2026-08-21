/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.w2k.blackbox.theming;

/**
 * <h1>ITheme</h1>
 * <p>Interface representing a Blackbox theme.</p>
 */
public interface ITheme
{
    /**
     * Get the internal name for this theme, which will be serialized in the Blackbox configuration.
     * @return  String
     */
    String getInternalName();

    /**
     * Get the display name for this theme, which will show up in the Blackbox.
     * @return  String
     */
    String getThemeName();

    /**
     * Get an optional description for this theme, which will show up when you hover over it in the Blackbox.
     * @return  String
     */
    String getThemeDescription();

    /**
     * Get the {@link IThemeType} that this theme will be grouped with.
     * @return  IThemeType
     */
    IThemeType getType();

    /**
     * Get the class for the Swing Look and Feel that this theme uses.
     * @return  String
     */
    String getThemeClass();

    /**
     * Determines whether this theme should be shown in the environment it's currently running in.
     * @return  True if the theme should show up
     */
    boolean isSupposedToShow();

    /**
     * Applies this theme.
     */
    void apply();

    /**
     * Display an optional message to the user when they switch to this theme. Currently unused.
     */
    void showOptionalMessage();
}
