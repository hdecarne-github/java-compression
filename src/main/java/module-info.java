/**
 * module-info
 */
module de.carne.compression {
	requires transitive org.eclipse.jdt.annotation;

	exports de.carne.nio.compression;
	exports de.carne.nio.compression.bzip2;
	exports de.carne.nio.compression.deflate;
	exports de.carne.nio.compression.lzma;
	exports de.carne.nio.compression.spi;
}
