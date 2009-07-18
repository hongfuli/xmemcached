package net.rubyeye.xmemcached.test.unittest.commands.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import net.rubyeye.xmemcached.CommandFactory;
import net.rubyeye.xmemcached.buffer.BufferAllocator;
import net.rubyeye.xmemcached.buffer.SimpleBufferAllocator;
import net.rubyeye.xmemcached.command.Command;
import net.rubyeye.xmemcached.command.CommandType;
import net.rubyeye.xmemcached.command.TextCommandFactory;
import net.rubyeye.xmemcached.transcoders.StringTranscoder;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import net.rubyeye.xmemcached.utils.ByteUtils;
import junit.framework.TestCase;

public class TextCommandFactoryTest extends TestCase {
	static final BufferAllocator bufferAllocator = new SimpleBufferAllocator();

	private CommandFactory commandFactory;

	@Override
	protected void setUp() throws Exception {
		this.commandFactory = new TextCommandFactory(
				);
	}

	public void testCreateDeleteCommand() {
		String key = "test";
		byte[] keyBytes = ByteUtils.getBytes(key);
		int time = 10;
		Command deleteCmd = commandFactory.createDeleteCommand("test",
				keyBytes, time,false);
		deleteCmd.encode(bufferAllocator);
		assertEquals(CommandType.DELETE, deleteCmd.getCommandType());
		String commandStr = new String(deleteCmd.getIoBuffer().getByteBuffer()
				.array());

		String expectedStr = "delete test 10\r\n";

		assertEquals(expectedStr, commandStr);
	}

	public void testCreateVersionCommand() {
		Command versionCmd = commandFactory.createVersionCommand(new CountDownLatch(1),null);
		versionCmd.encode(bufferAllocator);
		String commandStr = new String(versionCmd.getIoBuffer().getByteBuffer()
				.array());
		assertEquals("version\r\n", commandStr);
		assertEquals(CommandType.VERSION, versionCmd.getCommandType());
	}

	public void testCreateStoreCommand() {
		String key = "test";
		String value = "test";
		byte[] keyBytes = ByteUtils.getBytes(key);
		int exp = 0;
		Transcoder transcoder = new StringTranscoder();
		Command storeCmd = commandFactory.createSetCommand(key, keyBytes, exp, value,false, transcoder);
		storeCmd.encode(new SimpleBufferAllocator());
		assertFalse(storeCmd.isNoreply());
		assertEquals(CommandType.SET, storeCmd.getCommandType());
		String commandStr = new String(storeCmd.getIoBuffer().getByteBuffer()
				.array());

		String expectedStr = "set test " + StringTranscoder.STRING_FLAG
				+ " 0 4\r\ntest\r\n";
		assertEquals(expectedStr, commandStr);
	}

	public void testCreateGetCommand() {
		String key = "test";
		byte[] keyBytes = ByteUtils.getBytes(key);
		Command getCmd = commandFactory.createGetCommand(key, keyBytes,
				CommandType.GET_ONE);
		getCmd.encode(bufferAllocator);
		assertEquals(CommandType.GET_ONE, getCmd.getCommandType());
		String commandStr = new String(getCmd.getIoBuffer().getByteBuffer()
				.array());

		String expectedStr = "get test\r\n";
		assertEquals(expectedStr, commandStr);
	}

	public void testCreateIncrDecrCommand() {
		String key = "test";
		byte[] keyBytes = ByteUtils.getBytes(key);
		int num = 10;
		Command inCr = commandFactory.createIncrDecrCommand(key, keyBytes,
				num, CommandType.INCR,false);
		inCr.encode(bufferAllocator);
		assertEquals(CommandType.INCR, inCr.getCommandType());
		String commandStr = new String(inCr.getIoBuffer().getByteBuffer()
				.array());

		String expectedStr = "incr test 10\r\n";
		assertEquals(expectedStr, commandStr);

	}

	public void testCreateGetMultiCommand() {
		List<String> keys = new ArrayList<String>();
		keys.add("a");
		keys.add("b");
		keys.add("c");
		keys.add("a");

		Command cmd = commandFactory.createGetMultiCommand(keys, null,
				CommandType.GET_MANY, null);
		cmd.encode(bufferAllocator);
		assertEquals(CommandType.GET_MANY, cmd.getCommandType());
		String commandStr = new String(cmd.getIoBuffer().getByteBuffer()
				.array());

		String expectedStr = "get a b c a\r\n";
		assertEquals(expectedStr, commandStr);
	}

}
