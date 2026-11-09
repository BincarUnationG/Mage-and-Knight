package com.bincarunationg.mageknight.profile;

/**
 * 能提供玩家数据容器的对象。
 *
 * <p>这个接口由 mixin 注入到原版 {@code PlayerEntity} 上实现，
 * 作用是让访问层能把任意 {@code PlayerEntity} 转成数据容器持有者。</p>
 *
 * <p><b>业务代码不要直接用它</b>——统一使用 {@link Profiles#of}。</p>
 * 这个接口只服务于访问层的实现细节。
 */
public interface ProfileHolder {
	/**
	 * 取这个玩家自己的数据容器；不存在则创建。
	 * @return 永不返回 null
	 */
	PlayerProfile mk$profile();
}
