/*
 * This file is part of EssentialCmds, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 - 2015 HassanS6000
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.hsyyid.essentialcmds.cmdexecutors;

import com.google.common.collect.Lists;
import io.github.hsyyid.essentialcmds.internal.CommandExecutorBase;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

public class ListExecutor extends CommandExecutorBase
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Optional<PermissionService> optPermissionService = Sponge.getServiceManager().provide(PermissionService.class);

		src.sendMessage(Text.of(TextColors.GOLD, "There are ", TextColors.RED, Sponge.getServer().getOnlinePlayers().size(), TextColors.GOLD, " players online out of ", TextColors.GRAY, Sponge.getServer().getMaxPlayers()));

		if (optPermissionService.isPresent())
		{
			PermissionService permissionService = optPermissionService.get();

			for (Subject group : permissionService.getGroupSubjects().getAllSubjects())
			{
				Stream<Subject> users = StreamSupport.stream(permissionService.getUserSubjects().getAllSubjects().spliterator(), false).filter(u -> u.isChildOf(group));
				List<String> onlineUsers = Lists.newArrayList();
				Iterator<Subject> itr = users.iterator();

				while (itr.hasNext())
				{
					Subject user = itr.next();
					Optional<Player> optPlayer = Sponge.getServer().getPlayer(UUID.fromString(user.getIdentifier()));
					
					if (optPlayer.isPresent())
					{
						onlineUsers.add(optPlayer.get().getName());
					}
				}

				if (onlineUsers.size() > 0)
				{
					src.sendMessage(Text.of(TextColors.GOLD, group.getIdentifier(), ": ", TextColors.GRAY, onlineUsers.toString()));
				}
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.GRAY, Sponge.getServer().getOnlinePlayers().toString()));
		}

		return CommandResult.success();
	}

	@Nonnull
	@Override
	public String[] getAliases()
	{
		return new String[] { "list" };
	}

	@Nonnull
	@Override
	public CommandSpec getSpec()
	{
		return CommandSpec.builder()
			.description(Text.of("List Command"))
			.permission("essentialcmds.list.use")
			.executor(this)
			.build();
	}
}
