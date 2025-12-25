
-- ！！！ 慎重添加，当数据库中没有这些联系人记录时才可以执行该语句
 -- 基础联系人数据（1-10）
  INSERT INTO contacts (name, tele1, tele2, home, email, notes) VALUES
  ('张三', '13800138001', '15900159001', '北京市朝阳区', 'zhangsan@example.com', '公司同事'),
  ('李四', '13800138002', NULL, '上海市浦东新区', 'lisi@example.com', '大学同学'),
  ('王五', '13800138003', '15900159002', '广州市天河区', 'wangwu@example.com', '健身房朋友'),
  ('赵六', '13800138004', NULL, NULL, 'zhaoliu@example.com', '项目合作伙伴'),
  ('孙七', '13800138005', '15900159003', '深圳市南山区', NULL, '邻居'),
  ('周八', '13800138006', NULL, '杭州市西湖区', 'zhouba@example.com', NULL),
  ('吴九', '13800138007', '15900159004', NULL, 'wujiu@example.com', '客户'),
  ('郑十', '13800138008', NULL, '成都市武侯区', NULL, '供应商联系人'),
  ('钱一', '13800138009', '15900159005', '南京市鼓楼区', 'qianyi@example.com', '老朋友'),
  ('陈二', '13800138010', NULL, NULL, 'chener@example.com', '表弟');

  -- 英文名联系人（11-20）
  INSERT INTO contacts (name, tele1, tele2, home, email, notes) VALUES
  ('John Smith', '13900139001', '15900159006', 'Beijing Chaoyang', 'john.smith@example.com', 'Foreign colleague'),
  ('Mary Johnson', '13900139002', NULL, 'Shanghai Pudong', 'mary.j@example.com', 'Client manager'),
  ('刘明', '13900139003', '15900159007', '武汉市江汉区', 'liuming@example.com', '高中同学'),
  ('David Lee', '13900139004', NULL, NULL, 'david.lee@example.com', 'Business partner'),
  ('张伟', '13900139005', '15900159008', '西安市雁塔区', 'zhangwei@example.com', '表哥'),
  ('Sarah Chen', '13900139006', NULL, 'Shenzhen Nanshan', 'sarah.chen@example.com', NULL),
  ('王芳', '13900139007', '15900159009', '重庆市渝中区', 'wangfang@example.com', '同事'),
  ('Michael Wang', '13900139008', NULL, NULL, 'michael.w@example.com', 'Supplier'),
  ('李娜', '13900139009', '15900159010', '天津市和平区', NULL, '瑜伽教练'),
  ('Emily Zhang', '13900139010', NULL, 'Guangzhou Tianhe', 'emily.z@example.com', 'Designer');

  -- 多样化联系人（21-30）
  INSERT INTO contacts (name, tele1, tele2, home, email, notes) VALUES
  ('黄强', '15800158001', '18900189001', '青岛市市南区', 'huangqiang@example.com', '装修师傅'),
  ('林静', '15800158002', NULL, NULL, 'linjing@example.com', '医生'),
  ('何平', '15800158003', '18900189002', '长沙市岳麓区', NULL, '房东'),
  ('罗文', '15800158004', NULL, '厦门市思明区', 'luowen@example.com', '律师'),
  ('梁红', '15800158005', '18900189003', NULL, 'lianghong@example.com', '保险顾问'),
  ('谢军', '15800158006', NULL, '福州市鼓楼区', NULL, '快递员'),
  ('唐丽', '15800158007', '18900189004', '苏州市姑苏区', 'tangli@example.com', '美容师'),
  ('韩冰', '15800158008', NULL, NULL, 'hanbing@example.com', '摄影师'),
  ('曹阳', '15800158009', '18900189005', '宁波市海曙区','caoyang@example.com', '司机'),
  ('许敏', '15800158010', NULL, '无锡市梁溪区', NULL, '会计');


  -- 完整测试数据（31-40）
  INSERT INTO contacts (name, tele1, tele2, home, email, notes) VALUES
  ('Tom Brown', '18600186001', '13700137001', 'Beijing Haidian', 'tom.brown@example.com', 'IT consultant'),
  ('丁浩', '18600186002', NULL, '郑州市金水区', 'dinghao@example.com', '销售经理'),
  ('Jennifer Liu', '18600186003', '13700137002', NULL, 'jennifer.liu@example.com', 'HR manager'),
  ('冯涛', '18600186004', NULL, '济南市历下区', NULL, '工程师'),
  ('Alice Wang', '18600186005', '13700137003', 'Hangzhou Xihu', 'alice.wang@example.com', 'Product manager'),
  ('贾磊', '18600186006', NULL, NULL, 'jialei@example.com', '厨师'),
  ('Robert Chen', '18600186007', '13700137004', 'Chengdu Wuhou', 'robert.chen@example.com', 'Architect'),
  ('石娟', '18600186008', NULL, '合肥市蜀山区', 'shijuan@example.com', '教师'),
  ('Kevin Zhang', '18600186009', '13700137005', NULL, 'kevin.zhang@example.com', 'Consultant'),
  ('余静', '18600186010', NULL, '南昌市东湖区', NULL, '护士');


  INSERT INTO GROUPS (group_name, group_notes) VALUES
  ('家人', '家庭成员联系方式'),
  ('同事', '公司同事'),
  ('朋友', '个人朋友'),
  ('客户', '业务客户'),
  ('供应商', '合作供应商'),
  ('同学', '大学和高中同学'),
  ('邻居', '小区邻居'),
  ('服务人员', '各类服务提供者');
    -- 创建标签
  INSERT INTO tags (tag_color, tag_name, tag_notes) VALUES
  ('red', '重要', '重要联系人'),
  ('blue', '工作', '工作相关'),
  ('green', '生活', '日常生活'),
  ('yellow', '紧急', '紧急联系人'),
  ('purple', 'VIP', 'VIP客户'),
  ('orange', '待跟进', '需要跟进的联系人'),
  ('pink', '亲密', '亲密关系'),
  ('gray', '一般', '普通联系人');
  -- 清空关联表数据
  DELETE FROM tag_contacts;
  DELETE FROM contacts_group;
  DELETE FROM contacts;
  DELETE FROM tags;
  DELETE FROM GROUPS;



  -- 联系人-分组关联（使用INSERT OR IGNORE）
  -- 家人分组
  INSERT OR IGNORE INTO contacts_group (group_id, contacts_id) VALUES
  (1, 1), (1, 2), (1, 10);

  -- 同事分组
  INSERT OR IGNORE INTO contacts_group (group_id, contacts_id) VALUES
  (2, 1), (2, 4), (2, 7), (2, 11), (2, 12), (2, 17), (2, 32), (2, 33);

  -- 朋友分组
  INSERT OR IGNORE INTO contacts_group (group_id, contacts_id) VALUES
  (3, 3), (3, 9), (3, 13), (3, 15), (3, 19), (3, 20);

  -- 客户分组
  INSERT OR IGNORE INTO contacts_group (group_id, contacts_id) VALUES
  (4, 7), (4, 12), (4, 35);

  -- 供应商分组
  INSERT OR IGNORE INTO contacts_group (group_id, contacts_id) VALUES
  (5, 8), (5, 18);

  -- 同学分组
  INSERT OR IGNORE INTO contacts_group (group_id, contacts_id) VALUES
  (6, 2), (6, 13), (6, 15);

  -- 邻居分组
  INSERT OR IGNORE INTO contacts_group (group_id, contacts_id) VALUES
  (7, 5);

  -- 服务人员分组
  INSERT OR IGNORE INTO contacts_group (group_id, contacts_id) VALUES
  (8, 21), (8, 23), (8, 26), (8, 27), (8, 28), (8, 30), (8, 36), (8, 38), (8, 40);

  -- 联系人-标签关联（使用INSERT OR IGNORE）
  -- 重要标签
  INSERT OR IGNORE INTO tag_contacts (contacts_id, tag_id) VALUES
  (1, 1), (4, 1), (7, 1), (12, 1), (35, 1);

  -- 工作标签
  INSERT OR IGNORE INTO tag_contacts (contacts_id, tag_id) VALUES
  (4, 2), (7, 2), (8, 2), (11, 2), (12, 2), (17, 2), (18, 2), (32, 2), (33, 2), (35, 2);

  -- 生活标签
  INSERT OR IGNORE INTO tag_contacts (contacts_id, tag_id) VALUES
  (3, 3), (5, 3), (9, 3), (19, 3), (21, 3), (23, 3), (26, 3), (27, 3), (28, 3), (30, 3);

  -- 紧急标签
  INSERT OR IGNORE INTO tag_contacts (contacts_id, tag_id) VALUES
  (1, 4), (4, 4), (24, 4);

  -- VIP标签
  INSERT OR IGNORE INTO tag_contacts (contacts_id, tag_id) VALUES
  (12, 5), (35, 5);

  -- 待跟进标签
  INSERT OR IGNORE INTO tag_contacts (contacts_id, tag_id) VALUES
  (7, 6), (18, 6), (25, 6), (36, 6);

  -- 亲密标签
  INSERT OR IGNORE INTO tag_contacts (contacts_id, tag_id) VALUES
  (1, 7), (2, 7), (3, 7), (9, 7), (15, 7);

  -- 一般标签
  INSERT OR IGNORE INTO tag_contacts (contacts_id, tag_id) VALUES
  (6, 8), (14, 8), (16, 8), (20, 8), (22, 8), (29, 8), (31, 8), (34, 8), (37, 8), (39, 8);