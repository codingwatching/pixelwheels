/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.VersionInfo;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/** Main menu, shown at startup */
public class MainMenuScreen extends PwStageScreen {
    private final PwGame mGame;

    public MainMenuScreen(PwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        setupUi();
        new PwRefreshHelper(game, getStage()) {
            @Override
            protected void refresh() {
                mGame.showMainMenu();
            }
        };
    }

    private void setupUi() {
        boolean desktop = PlatformUtils.isDesktop();
        UiBuilder builder = new UiBuilder(mGame.getAssets().ui.atlas, mGame.getAssets().ui.skin);
        DifficultySelectorController.registerFactory(builder, mGame.getConfig());
        if (desktop) {
            builder.defineVariable("desktop");
        }

        AnchorGroup root = (AnchorGroup) builder.build(FileUtils.assets("screens/mainmenu.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        if (desktop) {
            builder.getActor("onePlayerButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    mGame.pushScreen(new SelectGameModeScreen(mGame, 1));
                                }
                            });
            builder.getActor("multiPlayerButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    mGame.pushScreen(new SelectPlayerCountScreen(mGame));
                                }
                            });
        } else {
            builder.getActor("quickRaceButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    mGame.showQuickRace(1);
                                }
                            });
            builder.getActor("championshipButton")
                    .addListener(
                            new MenuItemListener() {
                                @Override
                                public void triggered() {
                                    mGame.showChampionship(1);
                                }
                            });
        }
        builder.getActor("settingsButton")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                mGame.pushScreen(new ConfigScreen(mGame, ConfigScreen.Origin.MENU));
                            }
                        });
        builder.getActor("supportButton")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                mGame.pushScreen(new SupportScreen(mGame));
                            }
                        });
        if (desktop) {
            Menu menu = builder.getActor("menu");
            menu.addBackButton()
                    .addListener(
                            new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    Gdx.app.exit();
                                }
                            });
        }

        Label versionLabel = builder.getActor("version");
        versionLabel.setText(getShortenedVersion());
        versionLabel.pack();
    }

    private String getShortenedVersion() {
        return VersionInfo.VERSION.replace("alpha.", "a").replace("beta.", "b");
    }

    @Override
    public void onBackPressed() {
        Gdx.app.exit();
    }
}
