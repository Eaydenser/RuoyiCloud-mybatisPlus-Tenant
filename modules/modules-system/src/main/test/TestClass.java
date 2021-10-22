import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.utils.SecurityUtils;
import com.ruoyi.system.SystemApplication;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.*;
import org.apache.catalina.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SystemApplication.class)
public class TestClass {

    @Autowired
    ISysConfigService sysConfigService;

    @Autowired
    ISysDeptService sysDeptService;

    @Autowired
    ISysDictDataService sysDictDataService;

    @Autowired
    ISysDictTypeService sysDictTypeService;

    @Autowired
    ISysMenuService sysMenuService;

    @Autowired
    ISysNoticeService sysNoticeService;

    @Autowired
    ISysPostService sysPostService;

    @Autowired
    ISysRoleService sysRoleService;

    @Autowired
    ISysUserService sysUserService;

    @Test
    @Transactional
    @Rollback(false)
    public void get() {

    }
}
