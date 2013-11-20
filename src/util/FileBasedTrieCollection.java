package util;

import graph.core.UniqueID;

import java.io.File;
import java.util.Iterator;

import javax.activity.InvalidActivityException;

import org.apache.commons.io.FileUtils;

/**
 * A extension of a basic indexed collection which stores the collection in a
 * nested structure of files and folders.
 * 
 * @author Sam Sarjant
 */
public class FileBasedTrieCollection<T extends UniqueID, Serializable> extends
		HashIndexedCollection<T> {
	private static final String FILE_EXTENSION = ".dag";
	/** The root directory in which to store the graph. */
	private File root_;
	private DefaultSerialisationMechanism serialiser_;
	private int size_;

	public FileBasedTrieCollection(File rootDir) {
		super();
		root_ = rootDir;
		root_.mkdirs();
		serialiser_ = SerialisationMechanism.FST.getSerialiser();
	}

	public FileBasedTrieCollection(File rootDir, String typeDir) {
		super();
		rootDir.mkdir();
		root_ = new File(rootDir, typeDir + File.separatorChar);
		root_.mkdirs();
		serialiser_ = SerialisationMechanism.FST.getSerialiser();
	}

	public FileBasedTrieCollection(File rootDir, String typeDir, int initialSize) {
		super(initialSize);
		rootDir.mkdir();
		root_ = new File(rootDir, typeDir + File.separatorChar);
		root_.mkdirs();
		serialiser_ = SerialisationMechanism.FST.getSerialiser();
	}

	@Override
	public synchronized boolean add(T e) {
		boolean result = super.add(e);
		if (result) {
			size_++;
			try {
				serialiser_.serialize(e, generateFilePath(root_, e.getID()),
						false);
			} catch (InvalidActivityException e1) {
				e1.printStackTrace();
			}

		}
		return result;
	}

	@Override
	public void clear() {
		try {
			FileUtils.deleteDirectory(root_);
			root_.mkdirs();
			size_ = 0;
		} catch (Exception e) {
		}
	}

	@Override
	public boolean contains(Object o) {
		boolean result = super.contains(o);
		if (!result && o instanceof UniqueID)
			result = generateFilePath(root_, ((UniqueID) o).getID()).exists();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized T get(long id) {
		T element = super.get(id);
		if (element == null) {
			// Try loading it from file
			File location = generateFilePath(root_, id);
			try {
				element = (T) serialiser_.deserialize(location);
			} catch (InvalidActivityException e) {
				e.printStackTrace();
			}
			super.add(element);
		}
		return element;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && root_.list().length == 0;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Change to load up all files, then iterate via super method.
		return new Iterator<T>() {
			private Iterator<File> fileIter_ = FileUtils.iterateFiles(root_,
					null, true);

			@Override
			public boolean hasNext() {
				return fileIter_.hasNext();
			}

			@SuppressWarnings("unchecked")
			@Override
			public T next() {
				try {
					return (T) serialiser_.deserialize(fileIter_.next());
				} catch (InvalidActivityException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void remove() {
				fileIter_.remove();
			}
		};
	}

	@Override
	public synchronized boolean remove(Object o) {
		boolean result = super.remove(o);
		result |= generateFilePath(root_, ((UniqueID) o).getID()).delete();
		if (result)
			size_--;
		return result;
	}

	@Override
	public void setSize(int size) {
		size_ = size;
	}

	@Override
	public int size() {
		return size_;
	}

	@Override
	public synchronized void update(T element) {
		try {
			serialiser_.serialize(element,
					generateFilePath(root_, element.getID()), false);
		} catch (InvalidActivityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a file path given a numeric ID.
	 * 
	 * @param root
	 *            The root directory.
	 * @param id
	 *            The ID to generate a file path for.
	 * @return The File path to the ID ind@SuppressWarnings("unchecked") exed
	 *         file.
	 */
	public static File generateFilePath(File root, long id) {
		StringBuffer buffer = new StringBuffer();
		String strID = id + "";
		for (int i = 0; i < strID.length(); i++) {
			if (i > 0)
				buffer.append(File.separatorChar);
			buffer.append(strID.charAt(i));
		}
		buffer.append(FILE_EXTENSION);
		return new File(root, buffer.toString());
	}
}
